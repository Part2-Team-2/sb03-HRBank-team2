package org.yebigun.hrbank.domain.binaryContent.storage;

import org.springframework.http.ResponseEntity;
import org.yebigun.hrbank.domain.backup.Temporary.TempEmployeeDto;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : BackupBinaryContentStorage
 * Author       : dounguk
 * Date         : 2025. 6. 8.
 */
public interface BackupBinaryContentStorage {

    Long put(Long BinaryContentId, byte[] bytes);

    InputStream get(Long BinaryContentId);

    ResponseEntity<?> download(BinaryContentResponseDto response);

    //    BinaryContent putCsv(List<Employee> employees);
    BinaryContent putCsv(List<TempEmployeeDto> employees);

    BinaryContent putLog(long backupId, Exception e) throws IOException;
}

