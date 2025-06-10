package org.yebigun.hrbank.domain.backup.controller;

import com.querydsl.core.types.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.global.dto.ErrorResponse;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.controller
 * FileName     : BackupApi
 * Author       : dounguk
 * Date         : 2025. 6. 9.
 */
@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
public interface BackupApi {

    @Operation(summary = "데이터 백업 생성")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "백업 생성 성공",
            content = @Content(
                mediaType = "*/*",
                array = @ArraySchema(schema = @Schema(implementation = BackupDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "409",
            description = "이미 진행 중인 백업이 있음",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<BackupDto> createBackup(HttpServletRequest request);


    @Operation(summary = "데이터 백업 목록 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "*/*",
                array = @ArraySchema(schema = @Schema(implementation = CursorPageResponseBackupDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 지원하지 않는 정렬 필드",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping
    ResponseEntity<CursorPageResponseBackupDto> findAll(
        @Parameter(description = "작업자") String worker,
        @Parameter(description = "상태 (IN_PROGRESS, COMPLETED, FAILED)") BackupStatus status,
        @Parameter(description = "시작 시간(부터)") Instant startedAtFrom,
        @Parameter(description = "시작 시간(까지)") Instant startedAtTo,
        @Parameter(description = "이전 페이지 마지막 요소 ID") Long idAfter,
        @Parameter(description = "커서 (이전 페이지의 마지막 ID)") Instant cursor,
        @Parameter(description = "페이지 크기") int size,
        @Parameter(description = "정렬 필드 (startedAt, endedAt, status)") String sortField,
        @Parameter(description = "정렬 방향 (ASC, DESC)") Order sortDirection
    );
}
