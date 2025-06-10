package org.yebigun.hrbank.domain.employee.dto.data;

import java.time.LocalDate;

public record EmployeeTrendDto(
    LocalDate date,
    long count,
    long change,
    double changeRate
) {

}
