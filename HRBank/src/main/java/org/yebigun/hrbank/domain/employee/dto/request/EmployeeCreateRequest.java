package org.yebigun.hrbank.domain.employee.dto.request;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public record EmployeeCreateRequest(

    @NotBlank(message = "이름을 입력해주세요")
    @Size(max = 10, message = "이름은 10자 이내여야 합니다.")
    String name,

    @NotBlank(message = "이메일을 입력해주세요")
    @Email(message = "유효한 이메일 주소를 입력해주세요")
    @Size(max = 50, message = "이메일은 50자 이내여야 합니다.")
    String email,

    @NotNull(message = "부서를 선택해주세요")
    Long departmentId,

    @NotBlank(message = "직급을 입력해주세요")
    @Size(max = 50, message = "직급은 50자 이내여야 합니다.")
    String position,

    @NotNull(message = "입사일을 입력해주세요")
    LocalDate hireDate,

    @Size(max = 500, message = "메모는 500자 이내여야 합니다.")
    String memo

) {}
