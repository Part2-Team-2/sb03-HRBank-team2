package org.yebigun.hrbank.domain.binaryContent.storage;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;

import java.io.IOException;
import java.util.List;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : BackupBinaryContentStorage
 * Author       : dounguk
 * Date         : 2025. 6. 8.
 */
public interface BackupFileStorage {

    BinaryContent saveCsv(List<EmployeeDto> employees);

    BinaryContent saveLog(long backupId, Exception e) throws IOException;
}

