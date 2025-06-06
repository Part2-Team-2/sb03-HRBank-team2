package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

@RequiredArgsConstructor
@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate) {
        return employeeRepository.countByCondition(status, fromDate, toDate);
    }
}
