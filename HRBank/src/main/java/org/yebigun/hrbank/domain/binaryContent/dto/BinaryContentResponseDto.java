package org.yebigun.hrbank.domain.binaryContent.dto;

import lombok.Builder;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.dto
 * FileName     : BinaryContentResponse
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */

@Builder
public record BinaryContentResponseDto(
    Integer id,
    String fileName,
    Long size,
    String contentType
) {
}
