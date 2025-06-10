package org.yebigun.hrbank.domain.backup.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
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

    @Scheduled(cron = "0 0 * * * *")
    public void hourlyCreateBackup(){
        log.info("Starting hourly backup scheduler");
        try{
            backupService.createScheduledBackup();
        } catch (Exception e) {
            log.error("Hourly backup scheduler failed", e);
        }
        log.info("Closing hourly backup scheduler");
    }
}