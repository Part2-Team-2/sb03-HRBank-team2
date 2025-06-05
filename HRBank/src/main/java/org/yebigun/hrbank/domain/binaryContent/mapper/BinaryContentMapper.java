package org.yebigun.hrbank.domain.binaryContent.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponse;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.mapper
 * FileName     : BinaryContentMapper
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {
    BinaryContentMapper INSTANCE = Mappers.getMapper(BinaryContentMapper.class);
    BinaryContentResponse toDto(BinaryContent binaryContent);
}
