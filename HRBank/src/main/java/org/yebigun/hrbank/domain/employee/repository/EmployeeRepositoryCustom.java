package org.yebigun.hrbank.domain.employee.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeRepositoryCustom {

    List<EmployeeTrendDto> findEmployeeTrend(LocalDate from, LocalDate to, String unit);
    List<EmployeeDistributionDto> findEmployeeByStatusGroupByDepartmentOrPosition(String groupBy, EmployeeStatus status);
    Long countByCondition(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
    Map<Long, Long> countByDepartmentIds(List<Long> departmentIds);
    List<Employee> findAllByRequest(String nameOrEmail, String employeeNumber, String departmentName, String position, LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status, String cursor,
        int size, String sortField, String sortDirection);
    Long countByRequest(String nameOrEmail, String employeeNumber, String departmentName, String position, LocalDate hireDateFrom, LocalDate hireDateTo, EmployeeStatus status);
}
