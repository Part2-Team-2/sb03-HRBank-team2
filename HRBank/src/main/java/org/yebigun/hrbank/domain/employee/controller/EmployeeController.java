package org.yebigun.hrbank.domain.employee.controller;

import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.service.EmployeeService;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;

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
        @RequestPart(value = "profile", required = false) MultipartFile profile
    ) {
        EmployeeDto created = employeeService.createEmployee(request, profile);
        return ResponseEntity.ok(created);
    }


    @Override
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeDto> getEmployeeById(@PathVariable Long id) {
        EmployeeDto dto = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(dto);
    }

    @Override
    @GetMapping
    public ResponseEntity<CursorPageResponse<EmployeeDto>> findEmployees (
        @ModelAttribute EmployeeListRequest employeeListRequest){
        CursorPageResponse<EmployeeDto> result = employeeService.findEmployees(employeeListRequest);
        return ResponseEntity.status(HttpStatus.OK).body(result);

        }
    }