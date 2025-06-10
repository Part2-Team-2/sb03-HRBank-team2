package org.yebigun.hrbank.domain.department.service;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentCreateRequest;
import org.yebigun.hrbank.domain.department.dto.request.DepartmentUpdateRequest;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.department.mapper.DepartmentMapper;
import org.yebigun.hrbank.domain.department.repository.DepartmentRepository;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

@Log4j2
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

        return departmentMapper.toDto(createdDepartment, calculateEmployeeCount(createdDepartment));
    }

    @Override
    @Transactional(readOnly = true)
    public DepartmentDto findById(Long departmentId) {
        Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new NoSuchElementException("존재하지 않는 부서입니다"));

        return departmentMapper.toDto(department, calculateEmployeeCount(department));
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

        department.update(name, description, establishedDate);

        return departmentMapper.toDto(department, calculateEmployeeCount(department));
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

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<DepartmentDto> findDepartments(
        String nameOrDescription,
        Long idAfter,
        String cursor,
        Integer size,
        String sortField,
        String sortDirection
    ) {
        int pageSize = size != null ? size : 10;

        Long effectiveCursor = null;
        if (idAfter != null) {
            effectiveCursor = idAfter;
        } else if (cursor != null && !cursor.isBlank()) {
            effectiveCursor = decodeCursor(cursor);
        }

        List<Department> departments = departmentRepository.findNextDepartments(
            effectiveCursor, pageSize, sortField, sortDirection, nameOrDescription
        );

        boolean hasNext = departments.size() > pageSize;

        List<Department> currentPage = hasNext ? departments.subList(0, pageSize) : departments;

        Long nextIdAfter =
            hasNext && !currentPage.isEmpty() ? currentPage.get(pageSize - 1).getId() : null;

        String nextCursor = (nextIdAfter != null)
            ? Base64.getEncoder().encodeToString(String.valueOf(nextIdAfter).getBytes())
            : null;

        long totalElements = departmentRepository.countAllByCondition(nameOrDescription);

        List<Long> departmentIds = currentPage.stream()
            .map(Department::getId)
            .toList();

        Map<Long, Long> employeeCountMap = employeeRepository.countByDepartmentIds(departmentIds);

        List<DepartmentDto> dtoList = currentPage.stream()
            .map(dept -> departmentMapper.toDto(dept,
                    employeeCountMap.getOrDefault(dept.getId(), 0L).intValue()))
            .toList();

        return new CursorPageResponse<>
            (
                dtoList, nextIdAfter, nextCursor, pageSize, totalElements, hasNext
            );
    }

    private Long decodeCursor(String cursor) {
        if (cursor == null || cursor.isBlank()) {
            return null;
        }
        try {
            return Long.parseLong(new String(Base64.getDecoder().decode(cursor)));
        } catch (Exception e) {
            log.warn("Invalid cursor decode error", e);
            throw new IllegalArgumentException("잘못된 커서 형식입니다.");
        }
    }


    private int calculateEmployeeCount(Department department) {

        return employeeRepository.countByDepartmentId(department.getId());
    }
}
