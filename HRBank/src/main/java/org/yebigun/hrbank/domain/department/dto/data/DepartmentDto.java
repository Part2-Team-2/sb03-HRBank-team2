package org.yebigun.hrbank.domain.department.dto.data;

import java.time.LocalDate;
import lombok.Builder;

@Builder
public record DepartmentDto(
    Long id,
    String name,
    String description,
    LocalDate establishedDate,
    int employeeCount
) {
}
