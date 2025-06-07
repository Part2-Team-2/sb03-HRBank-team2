package org.yebigun.hrbank.domain.changelog.dto.data;

import java.time.Instant;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;

public record ChangeLogDto(
    Long id,
    ChangeType type,
    String employeeNumber,
    String memo,
    String ipAddress,
    Instant at
) {

}
