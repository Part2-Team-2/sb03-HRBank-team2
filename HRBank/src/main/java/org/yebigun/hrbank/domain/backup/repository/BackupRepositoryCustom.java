package org.yebigun.hrbank.domain.backup.repository;

import com.querydsl.core.types.Order;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;

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
        String worker, BackupStatus status, Instant startedAtFrom, Instant startedAtTo, Long idAfter, Instant cursor, int size, String sortField, Order sortDirection);

    long countByRequest(String worker, BackupStatus status, Instant startedAtFrom, Instant startedAtTo);
}
