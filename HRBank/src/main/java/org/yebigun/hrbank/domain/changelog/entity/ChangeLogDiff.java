package org.yebigun.hrbank.domain.changelog.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;

@Builder
@AllArgsConstructor
public class ChangeLogDiff {
    private final Long id;
    private final ChangeLog changeLogId;
    private final String propertyName;
    private final String before;
    private final String after;
}
