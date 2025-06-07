package org.yebigun.hrbank.domain.department.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record DepartmentUpdateRequest(

    @NotBlank(message = "부서명은 필수입니다.")
    @Size(max = 100, message = "부서명은 100자를 초과할 수 없습니다")
    String name,

    @Size(max = 500, message = "설명은 500자를 초과할 수 없습니다.")
    String description,

    @NotNull(message = "설립일은 필수입니다.")
    LocalDate establishedDate
) {

}
