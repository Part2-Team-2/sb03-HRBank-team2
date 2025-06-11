package org.yebigun.hrbank.domain.changelog.repository;

import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.global.dto.CursorPageResponse;

public interface ChangeLogRepositoryCustom {
    CursorPageResponse<ChangeLogDto> searchChangeLogs(ChangeLogSearchCondition condition);
}
