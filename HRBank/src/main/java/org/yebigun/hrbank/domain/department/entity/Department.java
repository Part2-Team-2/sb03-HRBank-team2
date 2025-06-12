package org.yebigun.hrbank.domain.department.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.yebigun.hrbank.global.base.BaseUpdatableEntity;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "department",
    indexes = {
        @Index(name = "idx_department_name_id", columnList = "name, id"),
        @Index(name = "idx_department_established_id", columnList = "establishedDate, id"),
        @Index(name = "idx_department_name", columnList = "name"),
        @Index(name = "idx_department_description", columnList = "description"),
    }
)
public class Department extends BaseUpdatableEntity {

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "established_date", nullable = false)
    private LocalDate establishedDate;

    public void update(String newName, String newDescription, LocalDate newEstablishedDate) {
        this.name = newName;
        this.description = newDescription;
        this.establishedDate = newEstablishedDate;
    }
}
