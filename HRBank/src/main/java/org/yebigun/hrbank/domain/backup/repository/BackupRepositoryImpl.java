package org.yebigun.hrbank.domain.backup.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class BackupRepositoryImpl implements BackupRepositoryCustom {
    private static final String STARTED_AT = "startedAt";
    private static final String ENDED_AT = "endedAt";

    private final JPAQueryFactory queryFactory;

    @Override
    public long countByRequest(String worker, BackupStatus status, Instant startedAtFrom, Instant startedAtTo) {
        QBackup qBackup = QBackup.backup;
        BooleanBuilder where = new BooleanBuilder();

        if (worker != null) {
            where.and(qBackup.employeeIp.contains(worker));
        }
        if (status != null) {
            where.and(qBackup.backupStatus.eq(status));
        }
        if (startedAtFrom != null) {
            where.and(qBackup.startedAtFrom.goe(startedAtFrom));
        }
        if (startedAtTo != null) {
            where.and(qBackup.startedAtTo.loe(startedAtTo));
        }

        Long totalElements = queryFactory
            .select(qBackup.count())
            .from(qBackup)
            .where(where)
            .fetchOne();

        return totalElements == null ? 0 : totalElements;

    }

    @Override
    public List<Backup> findAllByRequest(String worker, BackupStatus status, Instant startedAtFrom, Instant startedAtTo, Long idAfter, Instant cursor, int size, String sortField, Order sortDirection) {

        QBackup qBackup = QBackup.backup;
        BooleanBuilder where = new BooleanBuilder();

        if (worker != null) {
            where.and(qBackup.employeeIp.contains(worker));
        }
        if (status != null) {
            where.and(qBackup.backupStatus.eq(status));
        }
        if (startedAtFrom != null) {
            where.and(qBackup.startedAtFrom.goe(startedAtFrom));
        }
        if (startedAtTo != null) {
            where.and(qBackup.startedAtTo.loe(startedAtTo));
        }

        if (cursor != null && idAfter != null) {
            BooleanBuilder cursorCondition = new BooleanBuilder();
            if (STARTED_AT.equals(sortField)) {
                if (sortDirection == Order.ASC) {
                    cursorCondition.or(qBackup.startedAtFrom.gt(cursor));
                    cursorCondition.or(qBackup.startedAtFrom.eq(cursor).and(qBackup.id.gt(idAfter)));
                } else {
                    cursorCondition.or(qBackup.startedAtFrom.lt(cursor));
                    cursorCondition.or(qBackup.startedAtFrom.eq(cursor).and(qBackup.id.lt(idAfter)));
                }
            }
            else {// (ENDED_AT.equals(sortField))
                if (sortDirection == Order.ASC) {
                    cursorCondition.or(qBackup.startedAtTo.gt(cursor));
                    cursorCondition.or(qBackup.startedAtTo.eq(cursor).and(qBackup.id.gt(idAfter)));
                } else {
                    cursorCondition.or(qBackup.startedAtTo.lt(cursor));
                    cursorCondition.or(qBackup.startedAtTo.eq(cursor).and(qBackup.id.lt(idAfter)));
                }
            }
            where.and(cursorCondition);
        }

        OrderSpecifier<?> primaryOrder = getOrderSpecifier(sortField, sortDirection, qBackup);

        OrderSpecifier<?> secondaryOrder = sortDirection == Order.ASC ? qBackup.id.asc() : qBackup.id.desc();

        List<Backup> backups = queryFactory
            .selectFrom(qBackup)
            .where(where)
            .orderBy(primaryOrder, secondaryOrder)
            .limit(size + 1)
            .fetch();
        return backups;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, Order orderBy, QBackup backup) {
        return switch (sortField) {
            case STARTED_AT -> new OrderSpecifier<>(orderBy, backup.startedAtFrom);
            case ENDED_AT -> new OrderSpecifier<>(orderBy, backup.startedAtTo);
            default -> throw new IllegalArgumentException("잘못된 요청 또는 정렬필드");
        };
    }
}
