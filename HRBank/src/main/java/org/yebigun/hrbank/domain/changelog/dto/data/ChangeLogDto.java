package org.yebigun.hrbank.domain.changelog.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;

public record ChangeLogDto(
    Long id,
    @NotNull
    ChangeType type,
    @NotBlank
    String employeeNumber,
    String memo,
    String ipAddress,
    @NotNull
    Instant at
) {

}
