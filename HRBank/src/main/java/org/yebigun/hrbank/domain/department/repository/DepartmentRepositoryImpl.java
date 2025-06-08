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
    public List<Department> findNextDepartments(Long cursorId, int size, String sortField,
        String sortDirection, String nameOrDescription) {
        QDepartment department = QDepartment.department;

        BooleanExpression cursorPredicate = (cursorId != null) ? department.id.gt(cursorId) : null;

        BooleanExpression keywordPredicate = null;
        if (nameOrDescription != null && !nameOrDescription.isBlank()) {
            keywordPredicate = department.name.containsIgnoreCase(nameOrDescription)
                .or(department.description.containsIgnoreCase(nameOrDescription));
        }

        BooleanExpression whereClause = combine(cursorPredicate, keywordPredicate);
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(department, sortField, sortDirection);

        return queryFactory.selectFrom(department)
            .where(whereClause)
            .orderBy(orderSpecifier)
            .limit(size + 1)
            .fetch();
    }

    private BooleanExpression combine(BooleanExpression... expressions) {
        BooleanExpression result = null;
        for (BooleanExpression expr : expressions) {
            if (expr != null) {
                result = (result == null) ? expr : result.and(expr);
            }
        }
        return result;
    }

    private OrderSpecifier<?> getOrderSpecifier(QDepartment d, String field, String dir) {
        boolean desc = "desc".equalsIgnoreCase(dir);
        return switch (field) {
            case "name" -> desc ? d.name.desc() : d.name.asc();
            case "establishedDate" -> desc ? d.establishedDate.desc() : d.establishedDate.asc();
            default -> d.id.asc(); // 기본 정렬
        };
    }
}
