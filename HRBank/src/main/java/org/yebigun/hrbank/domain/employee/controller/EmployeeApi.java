package org.yebigun.hrbank.domain.employee.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.global.dto.CursorPageResponse;
import org.yebigun.hrbank.global.dto.ErrorResponse;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "직원 관리", description = "직원 관리 API")
public interface EmployeeApi {

    @Operation(summary = "직원 수 추이 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EmployeeTrendDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 지원하지 않는 시간 단위",
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
    ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
        @Parameter(description = "시작 일시(기본값: 현재로부터 unit 기준 12개 이전") LocalDate from,
        @Parameter(description = "종료 일시(기본값: 현재)") LocalDate to,
        @Parameter(description = "시간 단위(day, week, month, quarter, year, 기본값:month)") String unit
    );

    @Operation(summary = "직원 분포 조회")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "조회 성공",
            content = @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = EmployeeDistributionDto.class))
            )
        ),
        @ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 지원하지 않는 그룹화 기준",
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
    ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistribution(
        @Parameter(description = "그룹화 기준(department: 부서별, position: 직무별)") String groupBy,
        @Parameter(description = "직원 상태(재직 중, 휴직 중, 퇴사, 기본값: 재직 중)") EmployeeStatus status
    );

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

    @Operation(summary = "직원 목록 조회")
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
    ResponseEntity<CursorPageResponse<EmployeeDto>> findEmployees(
        @Parameter(description = "직원 목록 조회를 위한 파라미터") EmployeeListRequest employeeListRequest
    );

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

    @Operation(
        summary = "직원 상세 조회",
        description = "직원 ID로 직원 상세 정보를 조회합니다."
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description  = "조회 성공",
            content      = @Content(
                mediaType = MediaType.APPLICATION_JSON_VALUE,
                schema    = @Schema(implementation = EmployeeDto.class)
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description  = "직원을 찾을 수 없음",
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
    @GetMapping("/{id}")
    ResponseEntity<EmployeeDto> getEmployeeById(
        @Parameter(description = "직원 고유 ID", required = true)
        @PathVariable("id")
        Long id
    );

    @Operation(
        summary = "직원 삭제"
    )
    @ApiResponses({
        @ApiResponse(
            responseCode = "204",
            description  = "삭제 성공"
        ),
        @ApiResponse(
            responseCode = "404",
            description  = "직원을 찾을 수 없음",
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
    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteEmployee(@PathVariable Long id);
}
