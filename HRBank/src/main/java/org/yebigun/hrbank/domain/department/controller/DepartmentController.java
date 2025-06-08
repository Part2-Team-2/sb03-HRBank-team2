package org.yebigun.hrbank.domain.department.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
import org.yebigun.hrbank.domain.department.service.DepartmentService;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

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
        @RequestBody @Valid DepartmentUpdateRequest request
    ) {
        DepartmentDto department = departmentService.update(id, request);

        return ResponseEntity.status(HttpStatus.OK).body(department);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
        @PathVariable("id") Long id
    ) {
        departmentService.delete(id);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping
    public ResponseEntity<CursorPageResponse<DepartmentDto>> findDepartments(
        @RequestParam(required = false) String cursor,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "establishedDate") String sortField,
        @RequestParam(defaultValue = "asc") String sortDirection,
        @RequestParam(required = false) String nameOrDescription
    ) {
        CursorPageResponse<DepartmentDto> response = departmentService.findDepartments(
            cursor, size, sortField, sortDirection, nameOrDescription
        );
        return ResponseEntity.ok(response);
    }

}
