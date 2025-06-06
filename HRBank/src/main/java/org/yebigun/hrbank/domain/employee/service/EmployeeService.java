package org.yebigun.hrbank.domain.employee.service;

import java.time.LocalDate;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeService {

    long getEmployeeCount(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
