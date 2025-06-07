package org.yebigun.hrbank.domain.backup.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.service.BackupService;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

import java.time.LocalDate;
import java.util.UUID;

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

    @Operation(summary = "데이터 백업 생성")
    @PostMapping
    public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {
        // 삭제 필요
        dummyEmployees(10);

        BackupDto backup = backupService.createBackup(request);
        return ResponseEntity.status(200).body(backup);
    }

    // 더미 데이터
    public void dummyEmployees(int n) {
        for (int i = 1; i <= n+1; i++) {
            Employee employee = Employee.builder()
                .name("직원" + i)
                .email("employee" + i + "@test.com")
                .employeeNumber("EMP-" + UUID.randomUUID())
                .position("백엔드 개발자")
                .hireDate(LocalDate.now().minusDays(i * 5))
                .memo("자동 생성된 테스트 데이터")
                .status(EmployeeStatus.ACTIVE)
                .build();
            employeeRepository.save(employee);
        }

        System.out.println("직원 " + n + "명 저장 완료");
    }
}
