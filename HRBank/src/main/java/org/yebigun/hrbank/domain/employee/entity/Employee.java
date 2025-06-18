package org.yebigun.hrbank.domain.employee.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.department.entity.Department;
import org.yebigun.hrbank.global.base.BaseUpdatableEntity;

import java.time.LocalDate;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "employee")
public class Employee extends BaseUpdatableEntity {

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "emp_no", nullable = false, unique = true)
    private String employeeNumber;

    @Column(nullable = false)
    private String position;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column
    private String memo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @ColumnDefault("'ACTIVE'")
    private EmployeeStatus status;

    @OneToOne
    @JoinColumn(name = "profile_id")
    private BinaryContent profile;

    public void updateName(String name) {
        this.name = name;
    }

    public void updateEmail(String email) {
        this.email = email;
    }

    public void updateDepartment(Department department) {
        this.department = department;
    }

    public void updatePosition(String position) {
        this.position = position;
    }

    public void updateHireDate(LocalDate hireDate) {
        this.hireDate = hireDate;
    }

    public void updateMemo(String memo) {
        this.memo = memo;
    }

    public void updateStatus(EmployeeStatus status) {
        this.status = status;
    }

    public void updateProfile(BinaryContent profile) {
        this.profile = profile;
    }
}
