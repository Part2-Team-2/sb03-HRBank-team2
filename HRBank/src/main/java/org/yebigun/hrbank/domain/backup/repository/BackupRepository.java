package org.yebigun.hrbank.domain.backup.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.backup.entity.Backup;

import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.repository
 * FileName     : BackupRepository
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
public interface BackupRepository extends JpaRepository<Backup, Long> {
}
