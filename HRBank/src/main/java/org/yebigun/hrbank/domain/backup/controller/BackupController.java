package org.yebigun.hrbank.domain.backup.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.service.BackupService;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.controller
 * FileName     : BackupController
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/backups")
public class BackupController {
    private final BackupService backupService;

    @PostMapping
    public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {
        BackupDto backup = backupService.createBackup(request);
        return ResponseEntity.status(200).body(backup);
    }
}
