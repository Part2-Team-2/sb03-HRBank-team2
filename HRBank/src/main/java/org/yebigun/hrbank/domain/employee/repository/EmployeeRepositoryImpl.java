package org.yebigun.hrbank.domain.employee.repository;

import static com.querydsl.core.types.dsl.Expressions.numberTemplate;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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

        List<LocalDate> activeDates = queryFactory
            .select(e.createdAt)
            .from(e)
            .where(e.status.eq(EmployeeStatus.ACTIVE))
            .fetch()
            .stream()
            .map(instant -> instant.atZone(ZoneId.of("Asia/Seoul")).toLocalDate())
            .sorted()
            .toList();

        int index = 0;
        LocalDate current = from;
        long prevCnt = 0L;

        List<EmployeeTrendDto> trend = new ArrayList<>();

        while (!current.isAfter(to)) {

            while (index < activeDates.size() && !activeDates.get(index).isAfter(current)) {
                index++;
            }

            long cnt = index;

            long change = cnt - prevCnt;
            double changeRate = 0.0;

            if (prevCnt != 0) {
                changeRate = ((cnt - prevCnt) * 100.0) / prevCnt;
            }

            prevCnt = cnt;

            trend.add(new EmployeeTrendDto(current, cnt, change, changeRate));

            current = switch (unit) {
                case "day" -> current.plusDays(1);
                case "week" -> current.plusWeeks(1);
                case "month" -> current.plusMonths(1);
                case "quarter" -> current.plusMonths(3);
                case "year" -> current.plusYears(1);
                default -> current;
            };
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
}
