package org.yebigun.hrbank.domain.backup.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.global.base.BaseUpdatableEntity;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.entity
 * FileName     : Backup
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Backup extends BaseUpdatableEntity {

    @Column(name = "started_at_from", nullable = false)
    Instant startedAtFrom;

    @Column(name = "started_at_to", nullable = false)
    Instant startedAtTo;

    @Column(name = "employee_ip", nullable = false)
    String employeeIp;

    @Enumerated(EnumType.STRING)
    @Column(name = "backup_status", nullable = false)
    BackupStatus backupStatus;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "binary_content_id")
    BinaryContent binaryContent;

    public void updateFile(BinaryContent binaryContent) {
        if(binaryContent == null && this.binaryContent != null) {
            this.binaryContent = binaryContent;
        }
    }

}

