package org.yebigun.hrbank.domain.binaryContent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.repository
 * FileName     : BinaryContentRepository
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
public interface BinaryContentRepository extends JpaRepository<BinaryContent, Long> {
}
