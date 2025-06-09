package org.yebigun.hrbank.domain.changelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long> {

    CursorPageResponseChangeLogDto searchChangeLogs(ChangeLogSearchCondition condition);

}
