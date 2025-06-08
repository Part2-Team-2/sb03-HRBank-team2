package org.yebigun.hrbank.domain.department.service;

import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

public interface DepartmentService {

    DepartmentDto create(DepartmentCreateRequest request);

    DepartmentDto findById(Long departmentId);

    DepartmentDto update(Long departmentId, DepartmentUpdateRequest request);

    void delete(Long departmentId);

    CursorPageResponse<DepartmentDto> findDepartments(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
    );

}
