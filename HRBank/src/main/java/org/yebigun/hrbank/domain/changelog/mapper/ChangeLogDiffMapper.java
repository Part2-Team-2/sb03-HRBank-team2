package org.yebigun.hrbank.domain.changelog.mapper;

import org.mapstruct.Mapper;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLogDiff;

@Mapper(componentModel = "spring")
public interface ChangeLogDiffMapper {

    DiffDto toDto(ChangeLogDiff diff);

}
