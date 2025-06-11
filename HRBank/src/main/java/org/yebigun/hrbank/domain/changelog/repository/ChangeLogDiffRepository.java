package org.yebigun.hrbank.domain.changelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLogDiff;

@Repository
public interface ChangeLogDiffRepository extends JpaRepository<ChangeLogDiff,Long> {

}
