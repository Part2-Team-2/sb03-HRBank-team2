package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDistributionDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeTrendDto;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeService {

    List<EmployeeTrendDto> getEmployeeTrend(LocalDate from, LocalDate to, String unit);
    List<EmployeeDistributionDto> getEmployeeDistribution(String groupBy, EmployeeStatus status);

    Long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    EmployeeDto createEmployee(EmployeeCreateRequest request, MultipartFile profile);

}
