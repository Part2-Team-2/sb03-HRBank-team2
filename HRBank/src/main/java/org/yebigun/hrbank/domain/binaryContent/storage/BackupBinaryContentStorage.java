package org.yebigun.hrbank.domain.binaryContent.storage;

import org.yebigun.hrbank.domain.backup.Temporary.TempEmployeeDto;
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

    InputStream get(Long BinaryContentId);

    //    BinaryContent putCsv(List<Employee> employees);
    BinaryContent writeCsv(List<TempEmployeeDto> employees);

    BinaryContent writeLog(long backupId, Exception e) throws IOException;
}

