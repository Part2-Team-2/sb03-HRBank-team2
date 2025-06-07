package org.yebigun.hrbank.domain.backup.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.service
 * FileName     : BackupService
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public interface BackupService {
    BackupDto createBackup(HttpServletRequest request);

    void findAsACursor(String worker, String status, Instant startedAtFrom, Instant startedAtTo, long idAfter, long cursor, int size, int sortField, String sortDirection);
}
