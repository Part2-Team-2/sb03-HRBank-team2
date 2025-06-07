package org.yebigun.hrbank.domain.changelog.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "change_log")
public class ChangeLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "employee_number", nullable = false, length = 100)
    private String employeeNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 10)
    private ChangeType type;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

//    @Column(name = "at", nullable = false, columnDefinition = "TIMESTAMPTZ")
    private Instant at;

    @OneToMany(mappedBy = "changeLog", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChangeLogDiff> diffs;
}