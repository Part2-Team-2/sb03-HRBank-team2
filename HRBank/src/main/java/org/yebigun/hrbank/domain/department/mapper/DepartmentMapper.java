package org.yebigun.hrbank.domain.department.mapper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.yebigun.hrbank.domain.department.dto.data.DepartmentDto;
import org.yebigun.hrbank.domain.department.entity.Department;

@Component
@RequiredArgsConstructor
public class DepartmentMapper {

    public DepartmentDto toDto(Department department, int employeeCount) {

        return DepartmentDto.builder()
            .id(department.getId())
            .name(department.getName())
            .description(department.getDescription())
            .establishedDate(department.getEstablishedDate())
            .employeeCount(employeeCount)
            .build();
    }

}

