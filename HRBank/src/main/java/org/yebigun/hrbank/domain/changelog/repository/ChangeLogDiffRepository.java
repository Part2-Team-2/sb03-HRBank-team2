package org.yebigun.hrbank.domain.changelog.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLogDiff;

public interface ChangeLogDiffRepository extends JpaRepository<ChangeLogDiff,Long> {

    List<ChangeLogDiff> findByChangeLogId(ChangeLog changeLog);

}
