package org.yebigun.hrbank.domain.employee.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class EmployeeCreateRequest {
    private String name;
    private String email;
    private Long departmentId;
    private String position;
    private LocalDate hireDate;
    private String memo;

}
