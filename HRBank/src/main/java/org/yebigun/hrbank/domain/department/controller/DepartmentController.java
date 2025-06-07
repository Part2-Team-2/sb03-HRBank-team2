package org.yebigun.hrbank.domain.department.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
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

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDto> findById(
        @PathVariable("id") Long id
    ) {
        DepartmentDto department = departmentService.findById(id);

        return ResponseEntity.status(HttpStatus.OK).body(department);
    }

    @Override
    @PatchMapping("/{id}")
    public ResponseEntity<DepartmentDto> update(
        @PathVariable("id") Long id,
        @RequestBody DepartmentUpdateRequest request
    ) {
        DepartmentDto department = departmentService.update(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(department);
    }

}
