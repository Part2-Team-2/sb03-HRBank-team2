package org.yebigun.hrbank.domain.employee.repository;

import java.time.LocalDate;
import org.yebigun.hrbank.domain.employee.entity.EmployeeStatus;

public interface EmployeeRepositoryCustom {

    long countByCondition(EmployeeStatus status, LocalDate fromDate, LocalDate toDate);
}
