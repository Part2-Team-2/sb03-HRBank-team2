package org.yebigun.hrbank.domain.employee.dto.request;

import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

public record EmployeeUpdateRequest(
    String name,
    String email,
    Long departmentId,
    String position,
    LocalDate hireDate,
    EmployeeStatus status,
    String memo
) {}
