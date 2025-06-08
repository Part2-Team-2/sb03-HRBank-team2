package org.yebigun.hrbank.domain.department.repository;

import java.util.List;
import org.yebigun.hrbank.domain.department.entity.Department;

public interface DepartmentRepositoryCustom {

    List<Department> findNextDepartments(
        Long cursorId,
        int size,
        String sortField,
        String sortDirection,
        String nameOrDescription
    );

    long countAllByCondition(String nameOrDescription);

}
