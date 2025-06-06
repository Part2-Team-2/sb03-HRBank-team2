package org.yebigun.hrbank.domain.binaryContent.storage;

import org.springframework.http.ResponseEntity;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;
import org.yebigun.hrbank.domain.employee.entity.Employee;

import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

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

    Long putCsv(List<Employee> employees);
}
