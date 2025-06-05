package org.yebigun.hrbank.domain.binaryContent.storage;

import org.springframework.http.ResponseEntity;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;

import java.io.InputStream;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : BinaryContentStorage
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
public interface BinaryContentStorage {

    Long put(Long BinaryContentId, byte[] bytes);

    InputStream get(Long BinaryContentId);

    ResponseEntity<?> download(BinaryContentResponseDto response);
}
