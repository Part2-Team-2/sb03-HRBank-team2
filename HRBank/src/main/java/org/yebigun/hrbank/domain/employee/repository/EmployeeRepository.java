package org.yebigun.hrbank.domain.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {

    int countByDepartmentId(Long departmentId);
}
