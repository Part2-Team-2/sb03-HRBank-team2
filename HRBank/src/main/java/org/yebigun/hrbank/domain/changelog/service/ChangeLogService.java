package org.yebigun.hrbank.domain.changelog.service;

import org.yebigun.hrbank.domain.employee.entity.Employee;

public interface ChangeLogService {

    void updateEmployee(Employee employee, String memo, String ipAddress);

}
