package org.yebigun.hrbank.domain.department.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.mapper.DepartmentMapper;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;

@RequiredArgsConstructor
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    @Override
    @Transactional
    public DepartmentDto create(DepartmentCreateRequest request) {
        String name = request.name();
        String description = request.description();
        LocalDate establishedDate = request.establishedDate();

        if (departmentRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }

        Department department = Department.builder()
            .name(name)
            .description(description)
            .establishedDate(establishedDate)
            .build();

        Department createdDepartment = departmentRepository.save(department);

        return departmentMapper.toDto(createdDepartment);
    }
}
