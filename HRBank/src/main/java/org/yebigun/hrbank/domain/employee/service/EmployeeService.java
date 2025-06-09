package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

public interface EmployeeService {

    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);
    Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
    CursorPageResponse<EmployeeDto> findEmployees(EmployeeListRequest request);
}
