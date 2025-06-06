package org.yebigun.hrbank.domain.changelog.service;

import java.util.List;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.employee.entity.Employee;

public interface ChangeLogService {

    List<ChangeLogDto> getChangeLogByEmployee(Employee employee);

    List<DiffDto> getDiffByChangeLogId(ChangeLog changeLog);
}
