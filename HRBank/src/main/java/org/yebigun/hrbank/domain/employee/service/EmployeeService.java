package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.dto.request.EmployeeCreateRequest;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;


public interface EmployeeService {

    long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);

    EmployeeDto createEmployee(EmployeeCreateRequest request, MultipartFile profile);
}
