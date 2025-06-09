package org.yebigun.hrbank.domain.employee.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public record EmployeeListRequest(
    @Schema(description = "직원 이름 또는 이메일")
    String nameOrEmail,
    @Schema(description = "사원 번호")
    String employeeNumber,
    @Schema(description = "부서 이름")
    String departmentName,
    @Schema(description = "직함")
    String position,
    @Schema(description = "입사일 시작")
    LocalDate hireDateFrom,
    @Schema(description = "입사일 종료")
    LocalDate hireDateTo,
    @Schema(description = "상태(재직 중, 휴직 중, 퇴사)")
    EmployeeStatus status,
    @Schema(description = "이전 페이지의 마지막 요소 ID")
    Long idAfter,
    @Schema(description = "커서 (다음 페이지 시작점)")
    String cursor,
    @Schema(description = "페이지 크기", defaultValue = "10")
    Integer size,
    @Schema(description = "정렬 필드 (name, employeeNumber, hireDate)", defaultValue = "name")
    String sortField,
    @Schema(description = "정렬 방향(asc 또는 desc, 기본값: asc)", defaultValue = "asc")
    String sortDirection
) {
    // DefaultValue가 있는 필드 처리
    public EmployeeListRequest {
        if (size == null) size = 10;
        if (sortField == null) sortField = "name";
        if (sortDirection == null) sortDirection = "asc";
    }
}
