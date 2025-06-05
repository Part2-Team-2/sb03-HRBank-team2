package org.yebigun.hrbank.domain.backup.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.binaryContent.mapper.BinaryContentMapper;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.mapper
 * FileName     : BackupMapper
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */

@Mapper(componentModel = "spring", uses = {BinaryContentMapper.class})
public interface BackupMapper {
    BackupMapper INSTANCE = Mappers.getMapper(BackupMapper.class);

    BackupDto toDto(Backup backup);

}
