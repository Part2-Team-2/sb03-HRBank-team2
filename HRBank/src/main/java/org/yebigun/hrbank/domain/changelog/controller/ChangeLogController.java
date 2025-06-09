package org.yebigun.hrbank.domain.changelog.controller;

import java.time.Instant;
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
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.domain.changelog.service.ChangeLogService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    // 이력 목록 조회
    @GetMapping
    public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) ChangeType type,
        @RequestParam(required = false) String memo,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME)Instant atFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME)Instant atTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "at") String sortField,
        @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        // 커서 디코딩, idAfter 변환
        Long effectiveIdAfter = resolveCursor(cursor, idAfter);

        ChangeLogSearchCondition condition = new ChangeLogSearchCondition(
            employeeNumber, type, memo, ipAddress, atFrom, atTo, effectiveIdAfter, size, sortField, sortDirection
        );
        return ResponseEntity.ok(changeLogService.getChangeLogs(condition));
    }

    // 변경 상세 이력 조회
    @GetMapping("/{id}/diffs")
    public ResponseEntity<List<DiffDto>> getChangeLogDiffs(@PathVariable Long id) {
        return ResponseEntity.ok(changeLogService.getChangeLogDiffs(id));
    }

    // 커서 문자열 id 변환 메서드
    private Long resolveCursor(String cursor, Long idAfter) {
        if (cursor != null) {
            try {
                return Long.parseLong(new String(Base64.getDecoder().decode((cursor))));
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Invalid cursor provided: " + cursor);
            }
        }
        return idAfter;
    }

}
