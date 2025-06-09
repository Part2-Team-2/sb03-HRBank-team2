package org.yebigun.hrbank.domain.department.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "department")
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
