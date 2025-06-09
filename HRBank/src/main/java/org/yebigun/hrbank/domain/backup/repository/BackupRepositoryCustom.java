package org.yebigun.hrbank.domain.backup.repository;

import org.yebigun.hrbank.domain.backup.entity.Backup;

import java.time.Instant;
import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.repository
 * FileName     : BackupRepositoryCustom
 * Author       : dounguk
 * Date         : 2025. 6. 9.
 */

public interface BackupRepositoryCustom {
    List<Backup> findAllByRequest(
        String worker, String status, Instant startedAtFrom, Instant startedAtTo, Instant cursor, int size, String sortField, String sortDirection);
}
