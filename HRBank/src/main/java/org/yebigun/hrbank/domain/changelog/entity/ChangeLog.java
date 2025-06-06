package org.yebigun.hrbank.domain.changelog.entity;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Getter
@Builder
@AllArgsConstructor
public class ChangeLog {
    private final Long id;
    private final ChangeType type;
    private final Employee employee;
    private final String memo;
    private final String ipAddress;
    private final Instant at;

}