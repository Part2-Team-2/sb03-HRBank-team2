package org.yebigun.hrbank.domain.department.service;

import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;

public interface DepartmentService {

    DepartmentDto create(DepartmentCreateRequest request);

    DepartmentDto find(Long departmentId);

}
