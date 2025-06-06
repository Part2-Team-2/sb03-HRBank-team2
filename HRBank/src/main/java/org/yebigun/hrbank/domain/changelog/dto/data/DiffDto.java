package org.yebigun.hrbank.domain.changelog.dto.data;

public record DiffDto(
    String propertyName,
    String before,
    String after
) {

}
