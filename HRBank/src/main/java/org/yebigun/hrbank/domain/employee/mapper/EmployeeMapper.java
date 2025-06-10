package org.yebigun.hrbank.domain.employee.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(target = "department", ignore = true)
    @Mapping(target = "employeeNumber", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profile", ignore = true)
    Employee toEntity(EmployeeCreateRequest request);

    @Mapping(target = "departmentId",   source = "department.id")
    @Mapping(target = "departmentName", source = "department.name")
    @Mapping(target = "profileImageId", source = "profile.id")
    @Mapping(target = "status", source = "status")
    EmployeeDto toDto(Employee employee);
}