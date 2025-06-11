package org.yebigun.hrbank.domain.binaryContent.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.yebigun.hrbank.domain.binaryContent.storage.BinaryContentStorage;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.scheduler
 * FileName     : BinaryContentScheduler
 * Author       : dounguk
 * Date         : 2025. 6. 11.
 */
@Log4j2
@Component
@RequiredArgsConstructor
public class BinaryContentScheduler {
    private final BinaryContentStorage binaryContentStorage;

    @Scheduled(cron = "0 0 * * * *")
    public void scheduledCleanUpUnusedFiles() {
        log.info("Cleaning up unused files");
        int count = binaryContentStorage.deleteUnusedFiles();
        log.info("Deleted {} unused files", count);
    }
}
