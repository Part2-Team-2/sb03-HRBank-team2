package org.yebigun.hrbank.domain.changelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLogDiff;

public interface ChangeLogDiffRepository extends JpaRepository<ChangeLogDiff,Long> {

}
