package org.yebigun.hrbank.domain.changelog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.employee.entity.Employee;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    List<ChangeLog> findByEmployee(Employee employee);

}
