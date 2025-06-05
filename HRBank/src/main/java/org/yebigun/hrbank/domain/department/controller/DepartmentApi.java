package org.yebigun.hrbank.domain.department.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.global.dto.ErrorResponse;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;

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

}
