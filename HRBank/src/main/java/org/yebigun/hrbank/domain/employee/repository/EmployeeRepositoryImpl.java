package org.yebigun.hrbank.domain.employee.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.LocalDate;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.entity.QEmployee;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public long countByCondition(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
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
}
