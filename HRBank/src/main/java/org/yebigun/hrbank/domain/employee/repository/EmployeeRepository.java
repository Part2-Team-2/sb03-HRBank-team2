package org.yebigun.hrbank.domain.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long>, EmployeeRepositoryCustom {

    int countByDepartmentId(Long departmentId);

    /**
     * 주어진 이메일이 이미 저장되어 있는지 확인합니다.
     * @param email 검사할 이메일
     * @return 존재하면 true, 그렇지 않으면 false
     */
    boolean existsByEmail(String email);
}
