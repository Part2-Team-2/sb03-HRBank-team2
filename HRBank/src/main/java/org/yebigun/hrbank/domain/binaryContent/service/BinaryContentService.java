package org.yebigun.hrbank.domain.binaryContent.service;

import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponse;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.service
 * FileName     : BinaryContentService
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
public interface BinaryContentService {
    BinaryContentResponse find(Long binaryId);
    void create(Long id);
}
