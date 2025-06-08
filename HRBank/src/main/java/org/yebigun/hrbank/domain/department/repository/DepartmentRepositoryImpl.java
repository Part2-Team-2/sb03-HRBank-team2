package org.yebigun.hrbank.domain.department.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.entity.QDepartment;

@Repository
@RequiredArgsConstructor
public class DepartmentRepositoryImpl implements DepartmentRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Department> findNextDepartments(
        Long cursor,
        int size,
        String sortField,
        String sortDirection,
        String nameOrDescription
    ) {
        QDepartment department = QDepartment.department;

        BooleanExpression condition = null;
        if (nameOrDescription != null && !nameOrDescription.isBlank()) {
            condition = department.name.containsIgnoreCase(nameOrDescription)
                .or(department.description.containsIgnoreCase(nameOrDescription));
        }

        BooleanExpression cursorCondition = null;
        if (cursor != null) {
            cursorCondition = switch (sortField) {
                case "name" -> department.name.gt(department.name.coalesce("").substring(0)).and(department.id.gt(cursor));
                case "establishedDate" -> department.establishedDate.gt(department.establishedDate).or(department.id.gt(cursor));
                default -> department.id.gt(cursor);
            };
        }

        OrderSpecifier<?> orderSpecifier = ("desc".equalsIgnoreCase(sortDirection))
            ? switch (sortField) {
            case "name" -> department.name.desc();
            case "establishedDate" -> department.establishedDate.desc();
            default -> department.id.desc();
        }
            : switch (sortField) {
                case "name" -> department.name.asc();
                case "establishedDate" -> department.establishedDate.asc();
                default -> department.id.asc();
            };

        return queryFactory
            .selectFrom(department)
            .where(condition, cursorCondition)
            .orderBy(orderSpecifier)
            .limit(size + 1)
            .fetch();
    }

    @Override
    public long countAllByCondition(String nameOrDescription) {
        QDepartment department = QDepartment.department;

        BooleanExpression predicate = null;
        if (nameOrDescription != null && !nameOrDescription.isBlank()) {
            predicate = department.name.containsIgnoreCase(nameOrDescription)
                .or(department.description.containsIgnoreCase(nameOrDescription));
        }

        Long count = queryFactory.select(department.count())
            .from(department)
            .where(predicate)
            .fetchOne();

        return count != null ? count : 0L;
    }
}
