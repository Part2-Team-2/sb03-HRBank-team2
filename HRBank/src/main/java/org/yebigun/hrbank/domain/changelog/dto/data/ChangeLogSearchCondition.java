package org.yebigun.hrbank.domain.changelog.dto.data;

import java.time.Instant;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;

public record ChangeLogSearchCondition(
    String employeeNumber,
    ChangeType type,
    String memo,
    String ipAddress,
    Instant atFrom,
    Instant atTo,
    Long idAfter,
    int size,
    String sortField,
    String sortDirection
) {
}
