package org.yebigun.hrbank.domain.employee.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.core.types.dsl.StringPath;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.yebigun.hrbank.domain.department.entity.QDepartment;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.entity.QEmployee;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

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
                Expressions.numberTemplate(Double.class, "Round({0} * 100.0 / {1}, 1)", e.count(),
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
        QEmployee employee = QEmployee.employee;

        BooleanBuilder builder = new BooleanBuilder();

        if (status != null) {
            builder.and(employee.status.eq(status));
        }

        if (fromDate != null) {
            builder.and(employee.hireDate.goe(fromDate));

            if (toDate != null) {
                builder.and(employee.hireDate.loe(toDate));
            }
        }

        Long count = Optional.ofNullable(queryFactory
            .select(employee.count())
            .from(employee)
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
