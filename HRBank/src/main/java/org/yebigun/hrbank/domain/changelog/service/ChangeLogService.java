package org.yebigun.hrbank.domain.changelog.service;

import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.domain.employee.entity.Employee;

public interface ChangeLogService {

    void recordChangeLog(Employee beforeValue, Employee afterValue, String memo, String ipAddress, ChangeType changeType);

    // 이력 목록 조회
    CursorPageResponseChangeLogDto getChangeLogs(ChangeLogSearchCondition condition);
}
