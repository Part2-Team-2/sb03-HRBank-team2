package org.yebigun.hrbank.domain.employee.controller;

import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.changelog.service.ChangeLogService;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeUpdateRequest;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.service.EmployeeService;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;
    private final ChangeLogService changeLogService;
    private final EntityManager entityManager;
    
    @Override
    @GetMapping("/stats/trend")
    public ResponseEntity<List<EmployeeTrendDto>> getEmployeeTrend(
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required = false, defaultValue = "month") String unit) {

        List<EmployeeTrendDto> result = employeeService.getEmployeeTrend(from, to, unit);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    @GetMapping("/stats/distribution")
    public ResponseEntity<List<EmployeeDistributionDto>> getEmployeeDistribution(
        @RequestParam(required = false, defaultValue = "department") String groupBy,
        @RequestParam(required = false, defaultValue = "ACTIVE") EmployeeStatus status
    ) {
        List<EmployeeDistributionDto> result = employeeService.getEmployeeDistribution(groupBy,
            status);

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @Override
    @GetMapping("/count")
    public ResponseEntity<Long> getEmployeeCount(
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        Long count = employeeService.getEmployeeCount(status, fromDate, toDate);

        return ResponseEntity.status(HttpStatus.OK).body(count);
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> createEmployee(
        @RequestPart("employee") EmployeeCreateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile,
        HttpServletRequest httpRequest
    ) {
        EmployeeDto created = employeeService.createEmployee(request, profile);

        String ipAddress = extractClientIp(httpRequest);
        Employee employee = employeeService.getEmployeeEntityById(created.id());
        changeLogService.createRecord(employee, request.memo(), ipAddress);

        return ResponseEntity.ok(created);
    }


    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto dto = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(
        @PathVariable Long id,
        HttpServletRequest httpRequest
    ) {
        String ipAddress = extractClientIp(httpRequest);
        Employee beforeValue = employeeService.getEmployeeEntityById(id);

        employeeService.deleteEmployee(id);
        changeLogService.deleteRecord(beforeValue, ipAddress);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<EmployeeDto>> findEmployees(
        @ModelAttribute EmployeeListRequest employeeListRequest) {
        CursorPageResponse<EmployeeDto> result = employeeService.findEmployees(employeeListRequest);
        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @PatchMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EmployeeDto> updateEmployee(
        @PathVariable("id") Long employeeId,
        @RequestPart("employee") EmployeeUpdateRequest request,
        @RequestPart(value = "profile", required = false) MultipartFile profile,
        HttpServletRequest httpRequest
    ) {
        String ipAddress = extractClientIp(httpRequest);

        Employee beforeValue = employeeService.getEmployeeEntityById(employeeId);
        entityManager.detach(beforeValue); // 영속성 컨텍스트에서 분리

        EmployeeDto updated = employeeService.updateEmployee(employeeId, request, profile);

        entityManager.clear(); // 영속성 컨텍스트 초기화
        Employee afterValue = employeeService.getEmployeeEntityById(employeeId);

        changeLogService.updateRecord(beforeValue, afterValue, request.memo(), ipAddress);

        return ResponseEntity.ok(updated);
    }

    private String extractClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty()) {
            ip = ip.split(",")[0].trim();
        } else {
            ip = request.getRemoteAddr();
        }

        // IPv6 루프백 처리
        if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
            return "127.0.0.1";
        }

        return ip;
    }

}
