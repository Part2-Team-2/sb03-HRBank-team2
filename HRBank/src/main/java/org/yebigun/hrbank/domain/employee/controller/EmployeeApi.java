package org.yebigun.hrbank.domain.employee.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.time.LocalDate;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.global.dto.ErrorResponse;

@Tag(name = "직원 관리", description = "직원 관리 API")
public interface EmployeeApi {

    @Operation(summary = "직원 수 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = Long.class)
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
    ResponseEntity<Long> getEmployeeCount(
        @Parameter(description = "직원 상태(재직중, 휴직중, 퇴사)") EmployeeStatus status,
        @Parameter(description = "입사일 시작 (지정 시 해당 기간 내 입사한 직원 수 조회, 미지정 시 전체 직원 수 조회)")
        LocalDate fromDate,
        @Parameter(description = "입사일 종료 (fromDate와 함께 사용, 기본값: 현재 일시)") LocalDate toDate);

    @Operation(
        summary     = "직원 등록",
        description = "새로운 직원을 등록합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description  = "등록 성공",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = EmployeeDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description  = "잘못된 요청 또는 중복된 이메일",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description  = "부서를 찾을 수 없음",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description  = "서버 오류",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<EmployeeDto> createEmployee(
        @Parameter(description = "직원 등록용 데이터(JSON)")
        @Valid
        @RequestPart("employee")
        EmployeeCreateRequest employee,

        @Parameter(
            description = "프로필 이미지 파일 (optional)",
            schema = @Schema(type = "string", format = "binary")
        )
        @RequestPart(value = "profile", required = false)
        MultipartFile profile
    );
}
