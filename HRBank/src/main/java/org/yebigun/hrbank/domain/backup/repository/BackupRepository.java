package org.yebigun.hrbank.domain.backup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;

import java.util.Optional;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.repository
 * FileName     : BackupRepository
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public interface BackupRepository extends JpaRepository<Backup, Long>, BackupRepositoryCustom {

    Optional<Backup> findTopByBackupStatusOrderByStartedAtToDesc(BackupStatus backupStatus);



    Optional<Backup> findTopByBackupStatusOrderByCreatedAtDesc(BackupStatus status);
}
