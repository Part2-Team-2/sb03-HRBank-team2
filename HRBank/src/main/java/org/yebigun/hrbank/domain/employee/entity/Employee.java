package org.yebigun.hrbank.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yebigun.hrbank.global.base.BaseUpdatableEntity;

@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table
public class Employee extends BaseUpdatableEntity {

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    //private Department department;

    @Column(name = "emp_no", nullable = false)
    private String employeeNumber;

    @Column(nullable = false)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column
    private String memo;

    @Column(nullable = false)
    @Enumerated
    private EmployeeStatus status;

    //private BinaryContent profile;
}
