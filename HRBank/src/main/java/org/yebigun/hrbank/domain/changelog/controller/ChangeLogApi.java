package org.yebigun.hrbank.domain.changelog.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.Instant;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.global.dto.ErrorResponse;

@Tag(name = "직원 정보 수정 이력 관리", description = "직원 정보 수정 이력 관리 API")
public interface ChangeLogApi {

    @Operation(
        summary = "직원 정보 수정 이력 목록 조회",
        description = "직원 정보 수정 이력 목록을 조회합니다. 상세 변경 내용은 포함되지 않습니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = CursorPageResponseChangeLogDto.class),
                examples = @ExampleObject(value = """
                {
                    "content": [
                    {
                        "id": 1,
                        "type": "UPDATED",
                        "employeeNumber": "EMP-2023-001",
                        "memo": "직함 변경에 따른 수정",
                        "ipAddress": "192.168.0.1",
                        "at": "2023-01-01T12:00:00"
                    }
                ],
                "nextCursor": "eyJpZCI6MjB9",
                "nextIdAfter": 20,
                "size": 10,
                "totalElements": 100,
                "hasNext": true
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 지원하지 않는 정렬 필드",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                {
                    "timestamp": "2025-03-06T05:39:06.152068Z",
                    "status": 400,
                    "message": "잘못된 요청입니다.",
                    "details": "부서 코드는 필수입니다."
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                {
                    "timestamp": "2025-03-07T13:52:56.152167Z",
                    "status": 500,
                    "message": "서버 오류가 발생했습니다.",
                    "details": "내부 서버 오류"
                }
                """)
            )
        )
    })
    ResponseEntity<CursorPageResponseChangeLogDto> getChangeLogs(
        @Parameter(description = "대상 직원 사번") String employeeNumber,
        @Parameter(description = "이력 유형 ( CREATED, UPDATED, DELETED )") ChangeType type,
        @Parameter(description = "내용") String memo,
        @Parameter(description = "IP 주소") String ipAddress,
        @Parameter(description = "수정 일시( 부터 )")Instant atFrom,
        @Parameter(description = "수정 일시( 까지 )")Instant atTo,
        @Parameter(description = "이전 페이지 마지막 요소 ID") Long idAfter,
        @Parameter(description = "커서 ( 이전 페이지의 마지막 ID )") String cursor,
        @Parameter(description = "페이지 크기 ( 기본값: 10 )") Integer size,
        @Parameter(description = "정렬 필드 ( ipAddress, at ) 기본값 : at") String sortField,
        @Parameter(description = "정렬 필드 ( asc, desc ) 기본값 : desc") String sortDirection
    );

    @Operation(
        summary = "직원 정보 수정 이력 상세 조회",
        description = "직원 정보 수정 이력의 상세 정보를 조회합니다. 변경 상세 내용이 포함됩니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = DiffDto.class)),
                examples = @ExampleObject(value = """
                [
                    {
                        "propertyName": "직함",
                        "before": "사원",
                        "after": "대리"
                    }
                ]
                """)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "이력을 찾을 수 없음",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                {
                    "timestamp": "2025-03-06T05:39:06.152068Z",
                    "status": 404,
                    "message": "이력을 찾을 수 없습니다.",
                    "details": "해당 ID의 이력이 존재하지 않습니다."
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                {
                    "timestamp": "2025-03-07T14:43:34.152068Z",
                    "status": 500,
                    "message": "서버 오류가 발생했습니다.",
                    "details": "내부 서버 오류"
                }
                """)
            )
        )
    })
    ResponseEntity<List<DiffDto>> getChangeLogDiffs(
        @Parameter(description = "이력 ID", required = true) Long id
    );

    @Operation(
        summary = "수정 이력 건수 조회",
        description = "직원 정보 수정 이력 건수를 조회합니다. 파라미터를 제공하지 않으면 최근 일주일 데이터를 반환합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = Long.class),
                examples = @ExampleObject(value = "9007199254740991")
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 유효하지 않은 날짜 범위",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                {
                    "timestamp": "2025-03-06T05:39:06.152078Z",
                    "status": 400,
                    "message": "잘못된 요청입니다.",
                    "details": "날짜 범위가 올바르지 않습니다."
                }
                """)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(value = """
                {
                    "timestamp": "2025-03-06T05:39:06.152068Z",
                    "status": 500,
                    "message": "서버 오류가 발생했습니다.",
                    "details": "내부 서버 오류"
                }
                """)
            )
        )
    })
    ResponseEntity<Long> getChangeLogCount(
        @Parameter(description = "시작 일시 ( 기본값: 7일 전 )") Instant fromDate,
        @Parameter(description = "종료 일시 ( 기본값: 현재 )") Instant toDate
    );
}
