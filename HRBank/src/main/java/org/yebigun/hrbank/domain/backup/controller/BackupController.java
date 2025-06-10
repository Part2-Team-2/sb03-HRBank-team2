package org.yebigun.hrbank.domain.backup.controller;

import com.mysema.commons.lang.Pair;
import com.querydsl.core.types.Order;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.bind.annotation.*;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.domain.backup.service.BackupService;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.controller.EmployeeApi;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;
import org.yebigun.hrbank.global.dto.ErrorResponse;
import org.yebigun.hrbank.global.exception.CustomBackupSynchronizedException;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.controller
 * FileName     : BackupController
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("api/backups")
public class BackupController implements BackupApi {
    private final BackupService backupService;

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    boolean trigger = true;

@GetMapping("/latest")
public ResponseEntity<BackupDto> findLatest(@RequestParam(required = false, defaultValue = "COMPLETED") BackupStatus status) {
    BackupDto latest = backupService.findLatest(status);
    return ResponseEntity.status(HttpStatus.OK).body(latest);
}


    @PostMapping
    public ResponseEntity<BackupDto> createBackup(HttpServletRequest request) {

//        if(trigger) {
//            dummyEmployees(100);
//            trigger = false;
//        }

        BackupDto backup = backupService.createBackup(request);
//        for (int i = 0; i < 40; i++) {
//            backupService.createBackup(request);
//        }

//        ExecutorService executor = Executors.newFixedThreadPool(10);
//        List<Future<?>> futures = new ArrayList<>();
//
//        for (int i = 0; i < 40; i++) {
//            futures.add(executor.submit(() -> {
//                backupService.createBackup(request);  // 예외 그대로 던지게 둡니다.
//            }));
//        }
//
//        executor.shutdown();
//        try {
//            executor.awaitTermination(10, TimeUnit.SECONDS);
//
//            for (Future<?> future : futures) {
//                try {
//                    future.get(); // 예외가 있었다면 여기서 발생
//                } catch (ExecutionException e) {
//                    Throwable cause = e.getCause();
//                    if (cause instanceof CustomBackupSynchronizedException ex) {
//                        throw ex; // ❗️ 원래 예외 그대로 던짐 → 409로 응답됨
//                    }
//                    throw new RuntimeException("예상치 못한 예외 발생", cause);
//                }
//            }
//
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }

        return ResponseEntity.status(HttpStatus.OK).body(backup);
    }

    @GetMapping
    public ResponseEntity<CursorPageResponseBackupDto> findAll(
        @RequestParam(required = false) String worker,
        @RequestParam(required = false) BackupStatus status,
        @RequestParam(required = false) Instant startedAtFrom,
        @RequestParam(required = false) Instant startedAtTo,
        @RequestParam(required = false) Long idAfter,
        @RequestParam(required = false) Instant cursor,
        @RequestParam(required = false, defaultValue = "10") int size,
        @RequestParam(required = false, defaultValue = "startedAt") String sortField,
        @RequestParam(required = false, defaultValue = "DESC") Order sortDirection
    ) {
        CursorPageResponseBackupDto asACursor = backupService.findAsACursor(
            worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);

        return ResponseEntity.ok().body(asACursor);
    }




    private void createSameTime(int n, HttpServletRequest request) {
        ExecutorService executor = Executors.newFixedThreadPool(10);
        List<Future<?>> futures = new ArrayList<>();
        List<Throwable> exceptions = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < n; i++) {
            futures.add(executor.submit(() -> {
                try {
                    backupService.createBackup(request);
                } catch (Exception e) {
                    exceptions.add(e); // 예외 수집
                }
            }));
        }

        executor.shutdown();
        try {
            executor.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (!exceptions.isEmpty()) {
            throw new RuntimeException("동시 실행 중 예외 발생: " + exceptions.get(0).getMessage());
        }
    }


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
