package org.yebigun.hrbank.domain.binaryContent.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.dto
 * FileName     : BinaryContentResponse
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */

@Builder
public record BinaryContentResponse(
    Long id,
    String fileName,
    Long size,
    String contentType
) {
}
