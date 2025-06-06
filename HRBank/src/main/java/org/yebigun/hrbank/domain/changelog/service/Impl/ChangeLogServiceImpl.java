package org.yebigun.hrbank.domain.changelog.service.Impl;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogDto;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.changelog.mapper.ChangeLogDiffMapper;
import org.yebigun.hrbank.domain.changelog.mapper.ChangeLogMapper;
import org.yebigun.hrbank.domain.changelog.repository.ChangeLogDiffRepository;
import org.yebigun.hrbank.domain.changelog.repository.ChangeLogRepository;
import org.yebigun.hrbank.domain.changelog.service.ChangeLogService;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Service
@RequiredArgsConstructor
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogDiffRepository changeLogDiffRepository;
    private final ChangeLogMapper changeLogMapper;
    private final ChangeLogDiffMapper changeLogDiffMapper;

    @Override
    public List<ChangeLogDto> getChangeLogByEmployee(Employee employee) {
        return changeLogRepository.findByEmployee(employee).stream()
            .map(changeLogMapper::toDto)
            .toList();
    }

    @Override
    public List<DiffDto> getDiffByChangeLogId(ChangeLog changeLog) {
        return changeLogDiffRepository.findByChangeLogId(changeLog).stream()
            .map(changeLogDiffMapper::toDto)
            .toList();
    }
}
