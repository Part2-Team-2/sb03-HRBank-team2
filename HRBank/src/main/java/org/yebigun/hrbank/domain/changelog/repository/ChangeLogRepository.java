package org.yebigun.hrbank.domain.changelog.repository;

import java.time.Instant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;

public interface ChangeLogRepository extends JpaRepository<ChangeLog, Long>, ChangeLogRepositoryCustom {

    long countByAtBetween(Instant from, Instant to);

}
