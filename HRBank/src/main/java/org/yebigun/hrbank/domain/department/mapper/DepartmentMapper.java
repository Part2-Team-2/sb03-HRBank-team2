package org.yebigun.hrbank.domain.department.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {

    private final EmployeeRepository employeeRepository;

    public DepartmentDto toDto(Department department){
        return DepartmentDto.builder()
            .id(department.getId())
            .name(department.getName())
            .description(department.getDescription())
            .establishedDate(department.getEstablishedDate())
            .employeeCount(calculateEmployeeCount(department))
            .build();
    }

    private int calculateEmployeeCount(Department department) {

         int employeeCount = employeeRepository.countByDepartmentId(department.getId());
        return employeeCount;
    }
}

