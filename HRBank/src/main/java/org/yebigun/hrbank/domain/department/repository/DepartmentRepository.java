package org.yebigun.hrbank.domain.department.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.department.entity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long>, DepartmentRepositoryCustom {

    boolean existsByName(String name);

}
