package org.yebigun.hrbank.domain.changelog.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.changelog.mapper.ChangeLogMapper;
import org.yebigun.hrbank.domain.changelog.repository.ChangeLogDiffRepository;
import org.yebigun.hrbank.domain.changelog.repository.ChangeLogRepository;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogDiffRepository changeLogDiffRepository;
    private final ChangeLogMapper changeLogMapper;

}
