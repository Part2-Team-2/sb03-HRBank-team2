package org.yebigun.hrbank.domain.department.service;

import java.time.LocalDate;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.mapper.DepartmentMapper;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@RequiredArgsConstructor
@Service
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeRepository employeeRepository;

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

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto findById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다"));

        return departmentMapper.toDto(department);
    }

    @Override
    @Transactional
    public DepartmentDto update(Long departmentId, DepartmentUpdateRequest request) {
        String name = request.name();
        String description = request.description();
        LocalDate establishedDate = request.establishedDate();

        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 부서입니다."));

        if (!department.getName().equals(name) && departmentRepository.existsByName(name)) {
            throw new IllegalArgumentException("이미 존재하는 부서 이름입니다.");
        }

        department.setName(name);
        department.setDescription(description);
        department.setEstablishedDate(establishedDate);

        return departmentMapper.toDto(department);
    }

    @Override
    @Transactional
    public void delete(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(
                () -> new NoSuchElementException("존재하지 않는 부서입니다."));
        int employeeCount = employeeRepository.countByDepartmentId(departmentId);
        if (employeeCount > 0) {
            throw new IllegalArgumentException("소속 직원이 있는 부서는 삭제할 수 없습니다.");
        }

        departmentRepository.delete(department);
    }
}
