package org.yebigun.hrbank.domain.backup.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.domain.backup.entity.QBackup;

import java.time.Instant;
import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.repository
 * FileName     : BackupRepositoryCustomImpl
 * Author       : dounguk
 * Date         : 2025. 6. 9.
 */
@Repository
@RequiredArgsConstructor
public class BackupRepositoryCustomImpl implements BackupRepositoryCustom {
    private static final String STARTED_AT = "startedAt";
    private static final String ENDED_AT = "endedAt";
    private static final String STATUS = "status";

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Backup> findAllByRequest(String worker, String status, Instant startedAtFrom, Instant startedAtTo, Instant cursor, int size, String sortField, String sortDirection) {

        QBackup qBackup = QBackup.backup;
        BooleanBuilder where = new BooleanBuilder();
        if (worker != null) {
            where.and(qBackup.employeeIp.contains(worker));
        }
        if (status != null) {
            where.and(qBackup.backupStatus.eq(BackupStatus.valueOf(status)));
        }
        if (startedAtFrom != null) {
            where.and(qBackup.createdAt.goe(startedAtFrom));
        }
        if (startedAtTo != null) {
            where.and(qBackup.createdAt.loe(startedAtTo));
        }

        Order orderBy = sortDirection.equalsIgnoreCase("ASC") ? Order.ASC : Order.DESC;

        if (cursor != null) {
            if (STARTED_AT.equals(sortField)) {
                where.and(orderBy == Order.ASC
                    ? qBackup.startedAtFrom.gt(cursor) : qBackup.startedAtFrom.lt(cursor));
            } else if (ENDED_AT.equals(sortField)) {
                where.and(orderBy == Order.ASC
                    ? qBackup.startedAtTo.gt(cursor) : qBackup.startedAtTo.lt(cursor));
            } else if (STATUS.equals(sortField)) {
                where.and(orderBy == Order.ASC
                    ? qBackup.backupStatus.gt(BackupStatus.valueOf(status)) :
                    qBackup.backupStatus.lt(BackupStatus.valueOf(status))
                );
            } else {
                throw new IllegalArgumentException("잘못된 요청 또는 정렬필드");
            }
        }

        List<Backup> backups = queryFactory
            .selectFrom(qBackup)
            .where(where)
            .orderBy(getOrderSpecifier(sortField, orderBy, qBackup))
            .limit(size + 1)
            .fetch();
        return backups;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, Order orderBy, QBackup backup) {
        return switch (sortField) {
            case STARTED_AT -> new OrderSpecifier<>(orderBy, backup.startedAtFrom);
            case ENDED_AT -> new OrderSpecifier<>(orderBy, backup.startedAtTo);
            case STATUS -> new OrderSpecifier<>(orderBy, backup.backupStatus);
            default -> throw new IllegalArgumentException("잘못된 요청 또는 정렬필드");
        };
    }
}
