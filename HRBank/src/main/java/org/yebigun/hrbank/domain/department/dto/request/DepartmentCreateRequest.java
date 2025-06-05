package org.yebigun.hrbank.domain.department.dto.request;

import java.time.LocalDate;
import lombok.Builder;

public record DepartmentCreateRequest(
    String name,
    String description,
    LocalDate establishedDate
) {

}
