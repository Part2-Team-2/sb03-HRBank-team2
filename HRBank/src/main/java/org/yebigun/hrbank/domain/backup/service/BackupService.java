package org.yebigun.hrbank.domain.backup.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.service
 * FileName     : BackupService
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public interface BackupService {
    BackupDto createBackup(HttpServletRequest request);

}
