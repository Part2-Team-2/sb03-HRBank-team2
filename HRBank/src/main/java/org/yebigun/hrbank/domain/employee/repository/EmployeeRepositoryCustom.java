package org.yebigun.hrbank.domain.employee.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeRepositoryCustom {

    List<EmployeeTrendDto> findEmployeeTrend(LocalDate from, LocalDate to, String unit);
    List<EmployeeDistributionDto> findEmployeeByStatusGroupByDepartmentOrPosition(String groupBy, EmployeeStatus status);
    Long countByCondition(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
    Map<Long, Long> countByDepartmentIds(List<Long> departmentIds);
}
