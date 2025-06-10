package org.yebigun.hrbank.domain.employee.dto.data;

import java.time.LocalDate;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public record EmployeeDto(
    Long id,
    String name,
    String email,
    String employeeNumber,
    Long departmentId,
    String departmentName,
    String position,
    LocalDate hireDate,
    EmployeeStatus status,
    Long profileImageId
) {
}
