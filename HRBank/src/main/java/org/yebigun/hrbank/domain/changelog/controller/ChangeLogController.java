package org.yebigun.hrbank.domain.changelog.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.domain.changelog.service.ChangeLogService;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController implements ChangeLogApi {

    private final ChangeLogService changeLogService;

    // 이력 목록 조회
    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponse<ChangeLogDto>> getChangeLogs(
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) ChangeType type,
        @RequestParam(required = false) String memo,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME)Instant atFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME)Instant atTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") @Min(1) @Max(100) int size,
        @RequestParam(defaultValue = "at") @Pattern(regexp = "^(at|ipAddress)$") String sortField,
        @RequestParam(defaultValue = "desc") @Pattern(regexp = "^(asc|desc)$") String sortDirection
    ) {
        // 커서 디코딩, idAfter 변환
        Long effectiveIdAfter = resolveCursor(cursor, idAfter);

        ChangeLogSearchCondition condition = new ChangeLogSearchCondition(
            employeeNumber, type, memo, ipAddress, atFrom, atTo, effectiveIdAfter, size, sortField, sortDirection
        );
        return ResponseEntity.ok(changeLogService.getChangeLogs(condition));
    }

    // 변경 상세 이력 조회
    @Override
    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<DiffDto>> getChangeLogDiffs(@PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(changeLogService.getChangeLogDiffs(id));
    }

    // 이력 건수 조회
    @Override
    @GetMapping("/count")
    public ResponseEntity<Long> getChangeLogCount(
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME) Instant toDate
    ) {
        Instant now = Instant.now();
        // 삼항 연산자를 이용하여 기본값 보정
        Instant from = fromDate != null ? fromDate : now.minus(7, ChronoUnit.DAYS);
        Instant to = toDate != null ? toDate : now;

        long count = changeLogService.countAllChangeLogs(from, to);
        return ResponseEntity.ok(count);
    }

    // 커서 문자열 id 변환 메서드
    private Long resolveCursor(String cursor, Long idAfter) {
        if (cursor != null) {
            try {
                String decodedCursor = new String(Base64.getDecoder().decode(cursor), StandardCharsets.UTF_8);
                Long cursorId = Long.parseLong(decodedCursor);

                if (cursorId <= 0) {
                    throw new IllegalArgumentException("커서 ID가 유효하지 않습니다");
                }
                return cursorId;
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid cursor format");
            }
        }
        return idAfter;
    }

}
