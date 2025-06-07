package org.yebigun.hrbank.domain.backup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.service.BackupService;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;
import org.yebigun.hrbank.global.dto.ErrorResponse;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.controller
 * FileName     : BackupController
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
@Tag(name = "데이터 백업 관리", description = "데이터 백업 관리 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("api/backups")
public class BackupController {
    private final BackupService backupService;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    boolean trigger = true;


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
    @PostMapping
    public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {
        if(trigger) {
            dummyEmployees(100);
            trigger = false;
        }

        BackupDto backup = backupService.createBackup(request);
        return ResponseEntity.status(200).body(backup);
    }

    @GetMapping
    public ResponseEntity<?> findAll(
        @RequestParam(required = false) String worker,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) Instant startedAtFrom,
        @RequestParam(required = false) Instant startedAtTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) Instant cursor,
        @RequestParam int size,
        @RequestParam String sortField,
        @RequestParam String sortDirection
    ) {

        CursorPageResponseBackupDto asACursor = backupService.findAsACursor(worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);
        return ResponseEntity.ok().body(asACursor);
    }


    @Transactional
    public void dummyEmployees(int n) {
        dummyDepartments(); // 부서 중복 insert 방지
        List<Department> departments = departmentRepository.findAll();

        for (int i = 1; i <= n; i++) {
            Department randomDept = departments.get(i % departments.size());

            Employee employee = Employee.builder()
                .name("직원" + i)
                .email("employee" + i + "@test.com")
                .employeeNumber("EMP-" + UUID.randomUUID())
                .position("백엔드 개발자")
                .hireDate(LocalDate.now().minusDays(i * 5))
                .department(randomDept)
                .memo("자동 생성된 테스트 데이터")
                .status(EmployeeStatus.ACTIVE)
                .build();

            employeeRepository.save(employee);
        }

        System.out.println("직원 " + n + "명 저장 완료");
    }
    private void dummyDepartments() {
        Map<String, Department> existingDepartments = departmentRepository.findAll().stream()
            .collect(Collectors.toMap(Department::getName, d -> d));

        insertIfNotExists(existingDepartments, "백엔드 개발팀", "서버 개발을 담당하는 팀", LocalDate.of(2020, 1, 1));
        insertIfNotExists(existingDepartments, "프론트엔드 개발팀", "UI 개발을 담당하는 팀", LocalDate.of(2021, 1, 1));
        insertIfNotExists(existingDepartments, "기획팀", "서비스 기획을 담당하는 팀", LocalDate.of(2022, 1, 1));
    }

    private void insertIfNotExists(Map<String, Department> existingMap, String name, String description, LocalDate date) {
        if (!existingMap.containsKey(name)) {
            Department department = Department.builder()
                .name(name)
                .description(description)
                .establishedDate(date)
                .build();
            departmentRepository.save(department);
        }
    }





}
