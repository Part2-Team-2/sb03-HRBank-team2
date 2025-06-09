package org.yebigun.hrbank.domain.employee.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.entity.QEmployee;

@RequiredArgsConstructor
public class EmployeeRepositoryImpl implements EmployeeRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QEmployee e = QEmployee.employee;
    private static final QDepartment d = QDepartment.department;

    @Override
    public List<EmployeeDistributionDto> findEmployeeByStatusGroupByDepartmentOrPosition(
        String groupBy,
        EmployeeStatus status) {

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
    public List<Employee> findAllByRequest(String nameOrEmail, String employeeNumber,
        String departmentName, String position, LocalDate hireDateFrom, LocalDate hireDateTo,
        EmployeeStatus status, String cursor, int size, String sortField, String sortDirection) {

        BooleanBuilder builder = getBuilder(nameOrEmail, employeeNumber, departmentName, position,
            hireDateFrom, hireDateTo, status);
        
        if (cursor != null) {
            Comparable<?> cursorValue = parseCursorValue(sortField, cursor);

            switch (sortField) {
                case "name":
                    if (sortDirection.equalsIgnoreCase("ASC")) {
                        builder.and(e.name.gt((String) cursorValue));
                    } else {
                        builder.and(e.name.lt((String) cursorValue));
                    }
                    break;

                case "hireDate":
                    if (sortDirection.equalsIgnoreCase("ASC")) {
                        builder.and(e.hireDate.gt((LocalDate) cursorValue));
                    } else {
                        builder.and(e.hireDate.lt((LocalDate) cursorValue));
                    }
                    break;

                case "position":
                    if (sortDirection.equalsIgnoreCase("asc")) {
                        builder.and(e.position.gt((String) cursorValue));
                    } else {
                        builder.and(e.position.lt((String) cursorValue));
                    }
                    break;

                case "employeeNumber":
                    if (sortDirection.equalsIgnoreCase("asc")) {
                        builder.and(e.employeeNumber.gt((String) cursorValue));
                    } else {
                        builder.and(e.employeeNumber.lt((String) cursorValue));
                    }
                    break;

                default:
                    throw new IllegalArgumentException("지원하지 않는 sortField: " + sortField);
            }
        }


        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(e, sortField, sortDirection);

        List<Employee> employees = queryFactory
            .selectFrom(e)
            .innerJoin(e.department, d).fetchJoin()
            .where(builder)
            .orderBy(orderSpecifier)
            .limit(size + 1)
            .fetch();

        return employees;
    }

    @Override
    public Map<Long, Long> countByDepartmentIds(List<Long> departmentIds) {
        if (departmentIds == null || departmentIds.isEmpty()) {
            return Map.of();
        }

        List<DepartmentEmployeeCount> results = queryFactory
            .select(Projections.constructor(DepartmentEmployeeCount.class,
                e.department.id,
                e.count()
            ))
            .from(e)
            .where(e.department.id.in(departmentIds))
            .groupBy(e.department.id)
            .fetch();

        return results.stream()
            .collect(Collectors.toMap(
                DepartmentEmployeeCount::departmentId,
                DepartmentEmployeeCount::employeeCount
            ));
    }

    @Override
    public Long countByRequest(String nameOrEmail, String employeeNumber, String departmentName,
        String position, LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status) {

        BooleanBuilder builder = getBuilder(nameOrEmail, employeeNumber, departmentName, position,
            hireDateFrom, hireDateTo, status);
        
        Long totalCount = Optional.ofNullable(queryFactory
            .select(e.count())
            .from(e)
            .innerJoin(e.department, d)
            .where(builder)
            .fetchOne()).orElse(0L);

        return totalCount;
    }

    private Comparable<?> parseCursorValue(String sortField, String cursor) {
        return switch (sortField) {
            case "name" -> cursor;
            case "employeeNumber" -> cursor;

            case "hireDate" -> LocalDate.parse(cursor); // ISO 형식 (yyyy-MM-dd)

            default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다.");
        };
    }
    
    private OrderSpecifier<?> getOrderSpecifier(QEmployee e, String sortField, String sortDirection) {
        Order order = sortDirection.equalsIgnoreCase("asc") ? Order.ASC : Order.DESC;

        return switch (sortField) {
            case "name" -> new OrderSpecifier<>(order, e.name);
            case "employeeNumber" -> new OrderSpecifier<>(order, e.employeeNumber);
            case "hireDate" -> new OrderSpecifier<>(order, e.hireDate);
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다.");
        };
    }

    private BooleanBuilder getBuilder(String nameOrEmail, String employeeNumber, String departmentName,
        String position, LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status) {
        BooleanBuilder builder = new BooleanBuilder();
        
        if (nameOrEmail != null) {
            builder.and(
                e.name.containsIgnoreCase(nameOrEmail)
                    .or(e.email.containsIgnoreCase(nameOrEmail))
            );
        }

        if (employeeNumber != null) {
            builder.and(e.employeeNumber.containsIgnoreCase(employeeNumber));
        }

        if (departmentName != null) {
            builder.and(d.name.containsIgnoreCase(departmentName));
        }

        if (position != null) {
            builder.and(e.position.containsIgnoreCase(position));
        }

        if (hireDateFrom != null) {
            builder.and(e.hireDate.goe(hireDateFrom));
        }

        if (hireDateTo != null) {
            builder.and(e.hireDate.loe(hireDateTo));
        }

        if (status != null) {
            builder.and(e.status.eq(status));
        }

        return builder;
    }

    public static record DepartmentEmployeeCount(Long departmentId, Long employeeCount) {

    }
}
