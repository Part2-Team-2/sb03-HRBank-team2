package org.yebigun.hrbank.domain.changelog.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.changelog.entity.QChangeLog;
import org.yebigun.hrbank.domain.changelog.mapper.ChangeLogMapper;

@Repository
@RequiredArgsConstructor
public class ChangeLogRepositoryImpl implements ChangeLogRepositoryCustom{

    private final JPAQueryFactory queryFactory;
    private final ChangeLogMapper changeLogMapper;

    @Override
    public CursorPageResponseChangeLogDto searchChangeLogs(ChangeLogSearchCondition condition) {
        QChangeLog changeLog = QChangeLog.changeLog;

        // 검색 조건 동적 조립
        BooleanBuilder builder = new BooleanBuilder();

        // 사번, 메모, IP 주소 : 부분 일치
        // 시간 : 범위 조건(from ~ to)
        // 유형 : 완전 일치
        // 조건 동시 만족

        if (condition.employeeNumber() != null) {
            builder.and(changeLog.employeeNumber.containsIgnoreCase(condition.employeeNumber()));
        }
        if (condition.type() != null) {
            builder.and(changeLog.type.eq(condition.type()));
        }
        if (condition.memo() != null) {
            builder.and(changeLog.memo.containsIgnoreCase(condition.memo()));
        }
        if (condition.ipAddress() != null) {
            builder.and(changeLog.ipAddress.containsIgnoreCase(condition.ipAddress()));
        }
        if (condition.atFrom() != null) {
            builder.and(changeLog.at.goe(condition.atFrom()));
        }
        if (condition.atTo() != null) {
            builder.and(changeLog.at.loe(condition.atTo()));
        }
        if (condition.idAfter() != null) {
            builder.and(changeLog.id.lt(condition.idAfter()));
        }

        // 정렬 조건 적용
        OrderSpecifier<?> orderSpecifier = getOrderSpecifier(condition.sortField(), condition.sortDirection(), changeLog);

        // 데이터 조회 (limit +1 → 다음 페이지 존재 여부 파악 목적)
        List<ChangeLog> result = queryFactory.selectFrom(changeLog)
            .where(builder)
            .orderBy(orderSpecifier)
            .limit(condition.size() + 1)
            .fetch();

        // 다음 페이지 여부 판단
        boolean hasNext = result.size() > condition.size();
        List<ChangeLog> pageContent = hasNext ? result.subList(0, condition.size()) : result;

        // 다음 페이지용 커서 정보 (다음 페이지 요청 시 사용)
        Long nextId = hasNext ? pageContent.get(pageContent.size() - 1).getId() : null;
        String nextCursor = (nextId != null)
            ? Base64.getEncoder().encodeToString(String.valueOf(nextId).getBytes())
            : null;

        // DTO 매핑
        List<ChangeLogDto> dtoList = pageContent.stream()
            .map(changeLogMapper::toDto)
            .toList();

        return new CursorPageResponseChangeLogDto(
            dtoList,          // 현재 페이지 데이터
            nextCursor,       // 커서 인코딩 값
            nextId,           // 다음 요청용 ID
            condition.size(), // 요청한 페이지 크기
            result.size(),    // 실제 전체 fetch된 row 수
            hasNext           // 다음 페이지 여부
        );
    }

    // 정렬 필드 및 방향 동적 지정
    private OrderSpecifier<?> getOrderSpecifier(String sortField, String sortDirection, QChangeLog changeLog) {
        PathBuilder<ChangeLog> path = new PathBuilder<>(ChangeLog.class, "changeLog");
        Order order = "asc".equalsIgnoreCase(sortDirection) ? Order.ASC : Order.DESC;

        if ("ipAddress".equals(sortField)) {
            return new OrderSpecifier<>(order, path.getString("ipAddress"));
        } else if ("at".equals(sortField)) {
            return new OrderSpecifier<>(order, path.getComparable("at", Instant.class));
        } else {
            return new OrderSpecifier<>(order, path.getComparable("at", Instant.class));
        }
    }
}
