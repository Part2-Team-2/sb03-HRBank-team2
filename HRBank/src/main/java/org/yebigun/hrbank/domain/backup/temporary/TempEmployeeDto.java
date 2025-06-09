package org.yebigun.hrbank.domain.backup.temporary;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

import java.time.LocalDate;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.Temporary
 * FileName     : TempEmployeeDto
 * Author       : dounguk
 * Date         : 2025. 6. 7.
 */
@Builder
@Setter
@Getter
public class TempEmployeeDto {
    private Long id;
    private String employeeNumber;
    private String name;
    private String email;
    private Department department;
    private String position;
    private LocalDate hireDate;
    private EmployeeStatus status;
}
