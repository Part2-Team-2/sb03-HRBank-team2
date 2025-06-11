package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeListRequest;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

public interface EmployeeService {

    List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit);

    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);

    Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
    CursorPageResponse<EmployeeDto> findEmployees(EmployeeListRequest request);

    EmployeeDto createEmployee(EmployeeCreateRequest request, MultipartFile profile);

    void deleteEmployee(Long employeeId);

    EmployeeDto getEmployeeById(Long id);
}
