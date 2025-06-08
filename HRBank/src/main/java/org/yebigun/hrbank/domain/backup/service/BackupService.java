package org.yebigun.hrbank.domain.backup.service;

import jakarta.servlet.http.HttpServletRequest;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.service
 * FileName     : BackupService
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public interface BackupService {
    BackupDto createBackup(HttpServletRequest request);

    CursorPageResponseBackupDto findAsACursor(
        String worker, String status, Instant startedAtFrom, Instant startedAtTo, Long idAfter, Instant cursor, int size, String sortField, String sortDirection);
}
