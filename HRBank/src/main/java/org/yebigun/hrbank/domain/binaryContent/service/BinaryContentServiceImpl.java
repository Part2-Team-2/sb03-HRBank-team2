package org.yebigun.hrbank.domain.binaryContent.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.mapper.BinaryContentMapper;
import org.yebigun.hrbank.domain.binaryContent.repository.BinaryContentRepository;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.service
 * FileName     : BinaryContentServiceImpl
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */

@Service
@RequiredArgsConstructor
public class BinaryContentServiceImpl implements BinaryContentService {

    private final BinaryContentRepository binaryContentRepository;
    private final BinaryContentMapper binaryContentMapper;

    @Override
    public BinaryContentResponseDto find(Long binaryId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryId).orElse(null);
        return binaryContentMapper.toDto(binaryContent);
    }
}
