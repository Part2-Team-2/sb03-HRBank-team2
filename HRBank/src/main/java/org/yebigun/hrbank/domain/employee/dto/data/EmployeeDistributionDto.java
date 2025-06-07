package org.yebigun.hrbank.domain.employee.dto.data;

public record EmployeeDistributionDto(
    String groupKey,
    Long count,
    Double percentage
) {
}
