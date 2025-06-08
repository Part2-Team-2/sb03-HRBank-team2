package org.yebigun.hrbank.domain.changelog.dto.data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.Instant;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;

public record ChangeLogDto(
    Long id,
    @NotNull(message = "변경 타입은 필수입니다")
    ChangeType type,
    @NotBlank(message = "사원 번호는 필수입니다")
    String employeeNumber,
    String memo,
    String ipAddress,
    @NotNull(message = "수정시각은 필수입니다")
    Instant at
) {

}
