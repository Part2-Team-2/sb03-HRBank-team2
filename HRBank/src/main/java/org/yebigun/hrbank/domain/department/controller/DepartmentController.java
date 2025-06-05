package org.yebigun.hrbank.domain.department.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.service.DepartmentService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/departments")
public class DepartmentController implements DepartmentApi {

    private final DepartmentService departmentService;

    @Override
    @PostMapping
    public ResponseEntity<DepartmentDto> create(
        @RequestBody @Valid DepartmentCreateRequest request
    ) {
        DepartmentDto createdDepartment = departmentService.create(request);

        return ResponseEntity.status(HttpStatus.OK).body(createdDepartment);
    }

}
