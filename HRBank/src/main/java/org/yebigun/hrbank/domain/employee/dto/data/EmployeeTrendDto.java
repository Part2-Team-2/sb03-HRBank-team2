package org.yebigun.hrbank.domain.employee.dto.data;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    Long count,
    Long change,
    Double changeRate
) {

}
