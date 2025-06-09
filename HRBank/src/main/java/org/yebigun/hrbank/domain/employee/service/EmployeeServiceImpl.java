package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private static final Set<String> VALID_GROUP_BY = Set.of("department", "position");
    private static final Set<String> VALID_SORT_DIRECTION = Set.of("asc", "desc");

    @Override
    public List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy,
        EmployeeStatus status) {

        if (!VALID_GROUP_BY.contains(groupBy)) {
            throw new IllegalArgumentException("지원하지 않는 그룹화 기준입니다.");
        }

        List<EmployeeDistributionDto> employees = employeeRepository.findEmployeeByStatusGroupByDepartmentOrPosition(
            groupBy, status);

        return employees;
    }

    @Override
    public Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return employeeRepository.countByCondition(status, fromDate, toDate);
    }

    @Override
    @Transactional(readOnly = true)
    public CursorPageResponse<EmployeeDto> findEmployees(EmployeeListRequest request) {
        String nameOrEmail = request.nameOrEmail();
        String employeeNumber = request.employeeNumber();
        String departmentName = request.departmentName();
        String position = request.position();
        LocalDate hireDateFrom = request.hireDateFrom();
        LocalDate hireDateTo = request.hireDateTo();
        EmployeeStatus status = request.status();
        String cursor = request.cursor();
        int size = request.size();
        String sortField = request.sortField();
        String sortDirection = request.sortDirection();

        if (!VALID_SORT_DIRECTION.contains(sortDirection)) {
            throw new IllegalArgumentException("잘못된 정렬 방향입니다.");
        }

        List<Employee> employees = employeeRepository.findAllByRequest(
            nameOrEmail, employeeNumber, departmentName, position, hireDateFrom, hireDateTo, status,
            cursor,
            size, sortField, sortDirection
        );

        // cursor
        boolean hasNext = employees.size() > size;
        String nextCursor = hasNext ? getNextCursor(sortField, employees.get(employees.size() - 1)): null;
        Long nextIdAfter = hasNext ? employees.get(employees.size() - 1).getId() : null;


        // 추후 employeeMapper 구현 시 변경 예정
        List<EmployeeDto> employeeDtos = employees.stream()
            .map(employee -> new EmployeeDto(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getEmployeeNumber(),
                employee.getDepartment().getId(),
                employee.getDepartment().getName(),
                employee.getPosition(),
                employee.getHireDate(),
                employee.getStatus(),
                employee.getProfile() != null ? employee.getProfile().getId() : null
            ))
            .toList();

        long totalElements = employeeRepository.countByRequest(nameOrEmail, employeeNumber, departmentName,
            position, hireDateFrom, hireDateTo, status);

        CursorPageResponse<EmployeeDto> response = new CursorPageResponse<>(employeeDtos, nextIdAfter,
            nextCursor, size, totalElements, hasNext);

        return response;
    }

    private String getNextCursor(String sortField, Employee employee) {
        return switch (sortField) {
            case "name" -> employee.getName();
            case "employeeNumber" -> employee.getEmployeeNumber();
            case "hireDate" -> employee.getHireDate().toString();
            default -> throw new IllegalArgumentException("지원하지 않는 정렬 필드입니다.");
        };
    }
}
