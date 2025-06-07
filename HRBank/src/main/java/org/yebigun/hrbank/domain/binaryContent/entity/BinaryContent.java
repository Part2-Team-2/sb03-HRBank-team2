package org.yebigun.hrbank.domain.binaryContent.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.yebigun.hrbank.global.base.BaseEntity;


/**
 * PackageName  : org.yebigun.hrbank.binaryContent.entity
 * FileName     : BinaryConent
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BinaryContent extends BaseEntity {
    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "size", nullable = false)
    private Long size;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;
}
