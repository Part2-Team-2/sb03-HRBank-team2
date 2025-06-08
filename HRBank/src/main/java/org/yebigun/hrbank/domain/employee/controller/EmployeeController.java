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
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.service.EmployeeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;

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
    @GetMapping(path = "/count")
    public ResponseEntity<Long> getEmployeeCount(
        @RequestParam(required = false) EmployeeStatus status,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        if (toDate == null) {
            toDate = LocalDate.now();
        }

        long count = employeeService.getEmployeeCount(status, fromDate, toDate);

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

}
