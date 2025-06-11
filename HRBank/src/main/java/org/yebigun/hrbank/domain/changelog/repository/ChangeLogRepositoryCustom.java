package org.yebigun.hrbank.domain.changelog.repository;

import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;

public interface ChangeLogRepositoryCustom {
    CursorPageResponseChangeLogDto searchChangeLogs(ChangeLogSearchCondition condition);
}
