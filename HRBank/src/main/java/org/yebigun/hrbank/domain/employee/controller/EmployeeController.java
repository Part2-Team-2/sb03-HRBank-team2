package org.yebigun.hrbank.domain.employee.controller;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.service.EmployeeService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/employees")
public class EmployeeController implements EmployeeApi {

    private final EmployeeService employeeService;

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
}
