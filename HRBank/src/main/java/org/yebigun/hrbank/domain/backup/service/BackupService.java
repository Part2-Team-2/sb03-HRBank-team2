package org.yebigun.hrbank.domain.backup.service;

import com.querydsl.core.types.Order;
import jakarta.servlet.http.HttpServletRequest;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.service
 * FileName     : BackupService
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public interface BackupService {

    BackupDto createBackup(HttpServletRequest request);

    void createScheduledBackup() throws Exception;

    CursorPageResponseBackupDto findAsACursor(
        String worker, BackupStatus status, Instant startedAtFrom, Instant startedAtTo, Long idAfter, Instant cursor, int size, String sortField, Order sortDirection);

    BackupDto findLatest(BackupStatus status);
}
