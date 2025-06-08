package org.yebigun.hrbank.domain.changelog.controller;

import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.domain.changelog.service.ChangeLogService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/change-logs")
public class ChangeLogController {

    private final ChangeLogService changeLogService;

    @GetMapping
    public ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
        @RequestParam(required = false) String employeeNumber,
        @RequestParam(required = false) ChangeType type,
        @RequestParam(required = false) String memo,
        @RequestParam(required = false) String ipAddress,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME)Instant atFrom,
        @RequestParam(required = false) @DateTimeFormat(iso = ISO.DATE_TIME)Instant atTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "at") String sortField,
        @RequestParam(defaultValue = "desc") String sortDirection
    ) {
        ChangeLogSearchCondition condition = new ChangeLogSearchCondition(
            employeeNumber, type, memo, ipAddress, atFrom, atTo, idAfter, size, sortField, sortDirection
        );
        return ResponseEntity.ok(changeLogService.getChangeLogs(condition));
    }

}
