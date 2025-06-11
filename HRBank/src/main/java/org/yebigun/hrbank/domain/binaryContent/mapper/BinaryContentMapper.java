package org.yebigun.hrbank.domain.binaryContent.mapper;

import org.mapstruct.Mapper;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.mapper
 * FileName     : BinaryContentMapper
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */

@Mapper(componentModel = "spring")
public interface BinaryContentMapper {
    BinaryContentResponseDto toDto(BinaryContent binaryContent);
}
