package org.yebigun.hrbank.domain.changelog.dto.data;

import java.time.Instant;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.domain.employee.entity.Employee;

public record ChangeLogDto(
    Long id,
    ChangeType type,
    Employee employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {

}
