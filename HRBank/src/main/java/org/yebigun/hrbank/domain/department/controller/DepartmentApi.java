package org.yebigun.hrbank.domain.department.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
import org.yebigun.hrbank.global.dto.CursorPageResponse;
import org.yebigun.hrbank.global.dto.ErrorResponse;

@Tag(name = "부서 관리", description = "부서 관리 API")
public interface DepartmentApi {

    @Operation(summary = "부서 등록")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "등록 성공",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = DepartmentDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 중복된 이름",
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
    ResponseEntity<DepartmentDto> create(
        DepartmentCreateRequest request
    );

    @Operation(summary = "부서 상세 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = DepartmentDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "부서를 찾을 수 없음",
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
    ResponseEntity<DepartmentDto> findById(
        @Parameter(name = "id", description = "부서 ID", required = true) Long id
    );

    @Operation(summary = "부서 수정")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "수정 성공",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = DepartmentDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 중복된 이름",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "부서를 찾을 수 없음",
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
    public ResponseEntity<DepartmentDto> update(
        @Parameter(name = "id", description = "부서 ID", required = true) Long id,
        DepartmentUpdateRequest request
    );

    @Operation(summary = "부서 삭제")
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description = "삭제 성공"
        ),
        @ApiResponse(
            responseCode = "400",
            description = "소속 직원이 있는 부서는 삭제할 수 없음",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "부서를 찾을 수 없음",
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
    ResponseEntity<Void> delete(
        @Parameter(name = "id", description = "부서 ID", required = true) Long id
    );

    @Operation(summary = "부서 목록 조회", description = "부서 목록을 조회합니다.")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = CursorPageResponse.class)
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
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    ResponseEntity<CursorPageResponse<DepartmentDto>> findDepartments(
        @Parameter(description = "부서 이름 또는 설명") String nameOrDescription,
        @Parameter(description = "이전 페이지 마지막 요소 ID") Long idAfter,
        @Parameter(description = "커서 (다음 페이지 시작점)") String cursor,
        @Parameter(description = "페이지 크기 (기본값: 10)") Integer size,
        @Parameter(description = "정렬 필드 (name 또는 establishedDate)")String sortField,
        @Parameter(description = "정렬 방향 (asc 또는 desc, 기본값: asc)")String sortDirection
    );
}
