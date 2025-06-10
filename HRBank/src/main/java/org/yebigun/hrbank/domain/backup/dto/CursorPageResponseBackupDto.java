package org.yebigun.hrbank.domain.backup.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.dto
 * FileName     : BackupListDto
 * Author       : dounguk
 * Date         : 2025. 6. 8.
 */

@Builder
public record CursorPageResponseBackupDto(
    List<BackupDto> content,
    Instant nextCursor,
    long nextIdAfter,
    int size,
    long totalElements,
    boolean hasNext
) {
}
