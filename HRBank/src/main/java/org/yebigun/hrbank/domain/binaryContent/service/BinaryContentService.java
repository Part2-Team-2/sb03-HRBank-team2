package org.yebigun.hrbank.domain.binaryContent.service;

import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.service
 * FileName     : BinaryContentService
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
public interface BinaryContentService {
    BinaryContentResponseDto find(Long binaryId);
}
