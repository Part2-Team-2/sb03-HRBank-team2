package org.yebigun.hrbank.domain.backup.controller;

import com.querydsl.core.types.Order;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.domain.backup.service.BackupService;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.controller
 * FileName     : BackupController
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/backups")
public class BackupController implements BackupApi {
    private final BackupService backupService;

    @GetMapping("/latest")
    public ResponseEntity<BackupDto> findLatest(@RequestParam(required = false, defaultValue = "COMPLETED") BackupStatus status) {
        BackupDto latest = backupService.findLatest(status);
        return ResponseEntity.status(HttpStatus.OK).body(latest);
    }

    @PostMapping
    public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {
        BackupDto backup = backupService.createBackup(request);
        return ResponseEntity.status(HttpStatus.OK).body(backup);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseBackupDto> findAll(
        @RequestParam(required = false) String worker,
        @RequestParam(required = false) BackupStatus status,
        @RequestParam(required = false) Instant startedAtFrom,
        @RequestParam(required = false) Instant startedAtTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) Instant cursor,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "startedAt") String sortField,
        @RequestParam(required = false, defaultValue = "DESC") Order sortDirection
    ) {
        CursorPageResponseBackupDto asACursor = backupService.findAsACursor(
            worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);
        return ResponseEntity.ok().body(asACursor);
    }
}

