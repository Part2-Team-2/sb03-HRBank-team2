package org.yebigun.hrbank.domain.employee.repository;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.yebigun.hrbank.domain.department.entity.QDepartment;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.entity.QEmployee;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<EmployeeTrendDto> findEmployeeTrend(LocalDate from, LocalDate to, String unit) {
        QEmployee e = QEmployee.employee;

        // 1. 필요한 날짜들만 조회 (전체 데이터 대신 날짜별 카운트)
        Map<LocalDate, Long> dateCounts = new HashMap<>();

        // 각 날짜별로 해당 날짜까지의 누적 카운트를 DB에서 직접 계산
        LocalDate current = from;
        while (!current.isAfter(to)) {
            Long count = queryFactory
                .select(e.count())
                .from(e)
                .where(e.status.eq(EmployeeStatus.ACTIVE)
                    .and(e.createdAt.loe(current.atStartOfDay(ZoneId.systemDefault()).toInstant())))
                .fetchOne();

            dateCounts.put(current, count != null ? count : 0L);

            current = switch (unit) {
                case "day" -> current.plusDays(1);
                case "week" -> current.plusWeeks(1);
                case "month" -> current.plusMonths(1);
                case "quarter" -> current.plusMonths(3);
                case "year" -> current.plusYears(1);
                default -> current.plusMonths(1);
            };
        }

        // 2. 변화량 계산
        List<EmployeeTrendDto> trend = new ArrayList<>();
        long prevCount = 0L;

        for (Map.Entry<LocalDate, Long> entry : dateCounts.entrySet().stream()
            .sorted(Map.Entry.comparingByKey()).toList()) {

            LocalDate date = entry.getKey();
            long count = entry.getValue();
            long change = count - prevCount;
            double changeRate = 0.0;

            if (prevCount != 0) {
                changeRate = (count - prevCount) * 100.0 / prevCount;
                changeRate = Math.round(changeRate * 10.0) / 10.0;
            }

            trend.add(new EmployeeTrendDto(date, count, change, changeRate));
            prevCount = count;
        }

        return trend;
    }

    @Override
    public List<EmployeeDistributionDto> findEmployeeByStatusGroupByDepartmentOrPosition(
        String groupBy,
        EmployeeStatus status) {
        QEmployee e = QEmployee.employee;
        QDepartment d = QDepartment.department;

        Long totalCount = Optional.ofNullable(queryFactory
            .select(e.count())
            .from(e)
            .where(e.status.eq(status))
            .fetchOne()).orElse(0L);

        if (totalCount == 0) {
            return new ArrayList<>();
        }

        StringPath path = groupBy.equals("department") ? d.name : e.position;

        List<EmployeeDistributionDto> result = queryFactory
            .select(Projections.constructor(EmployeeDistributionDto.class,
                path,
                e.count(),
                numberTemplate(Double.class, "Round({0} * 100.0 / {1}, 1)", e.count(),
                    totalCount)
            ))
            .from(e)
            .leftJoin(d).on(e.department.id.eq(d.id))
            .where(e.status.eq(status))
            .groupBy(path)
            .fetch();

        return result;
    }

    @Override
    public Long countByCondition(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        QEmployee e = QEmployee.employee;

        BooleanBuilder builder = new BooleanBuilder();

        if (status != null) {
            builder.and(e.status.eq(status));
        }

        if (fromDate != null) {
            builder.and(e.hireDate.goe(fromDate));

            if (toDate != null) {
                builder.and(e.hireDate.loe(toDate));
            }
        }

        Long count = Optional.ofNullable(queryFactory
            .select(e.count())
            .from(e)
            .where(builder)
            .fetchOne()).orElse(0L);

        return count;
    }

    @Override
    public Map<Long, Long> countByDepartmentIds(List<Long> departmentIds) {
        QEmployee employee = QEmployee.employee;

        if (departmentIds == null || departmentIds.isEmpty()) {
            return Map.of();
        }

        List<DepartmentEmployeeCount> results = queryFactory
            .select(Projections.constructor(DepartmentEmployeeCount.class,
                employee.department.id,
                employee.count()
            ))
            .from(employee)
            .where(employee.department.id.in(departmentIds))
            .groupBy(employee.department.id)
            .fetch();

        return results.stream()
            .collect(Collectors.toMap(
                DepartmentEmployeeCount::departmentId,
                DepartmentEmployeeCount::employeeCount
            ));
    }

        public static record DepartmentEmployeeCount(Long departmentId, Long employeeCount) {

    }

}
