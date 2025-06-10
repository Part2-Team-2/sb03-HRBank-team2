package org.yebigun.hrbank.domain.backup.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.backup.service.BackupService;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.scheduler
 * FileName     : HourlyBackupScheduler
 * Author       : dounguk
 * Date         : 2025. 6. 10.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class HourlyBackupScheduler {
    private final BackupService backupService;

    @Transactional
    @Scheduled(cron = "0 0 * * * *")
    public void hourlyCreateBackup() throws Exception {
        log.info("Starting hourly backup scheduler");
        backupService.createScheduledBackup();
        log.info("Closing hourly backup scheduler");
    }
}