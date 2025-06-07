package org.yebigun.hrbank.domain.changelog.mapper;

import org.mapstruct.Mapper;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;

@Mapper(componentModel = "spring")
public interface ChangeLogMapper {

    ChangeLogDto toDto(ChangeLog changeLog);

}
