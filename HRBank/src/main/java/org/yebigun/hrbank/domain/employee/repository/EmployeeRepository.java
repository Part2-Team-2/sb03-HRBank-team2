package org.yebigun.hrbank.domain.employee.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
    EmployeeRepositoryCustom {

    int countByDepartmentId(Long departmentId);

    boolean existsByEmail(String email);

    boolean existsByEmployeeNumber(String employeeNumber);

    boolean existsById(Long id);

    Optional<Employee> findTopByOrderByCreatedAtDesc();

    Optional<Employee> findTopByOrderByUpdatedAtDesc();
}

