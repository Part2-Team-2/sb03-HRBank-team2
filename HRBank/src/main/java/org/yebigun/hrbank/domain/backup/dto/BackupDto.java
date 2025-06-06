package org.yebigun.hrbank.domain.backup.dto;

import org.yebigun.hrbank.domain.backup.entity.BackupStatus;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.dto
 * FileName     : BackupDto
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public record BackupDto(
    Long id,
    String worker,
    Instant startedAt,
    Instant endedAt,
    BackupStatus status,
    Integer fileId
) {
}
