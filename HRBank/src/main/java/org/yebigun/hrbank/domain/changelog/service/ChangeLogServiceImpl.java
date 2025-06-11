package org.yebigun.hrbank.domain.changelog.service;

import jakarta.persistence.EntityNotFoundException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.changelog.dto.data.ChangeLogSearchCondition;
import org.yebigun.hrbank.domain.changelog.dto.data.DiffDto;
import org.yebigun.hrbank.domain.changelog.dto.response.CursorPageResponseChangeLogDto;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLogDiff;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
import org.yebigun.hrbank.domain.changelog.entity.PropertyName;
import org.yebigun.hrbank.domain.changelog.repository.ChangeLogDiffRepository;
import org.yebigun.hrbank.domain.changelog.repository.ChangeLogRepository;
import org.yebigun.hrbank.domain.employee.entity.Employee;

@Service
@RequiredArgsConstructor
@Transactional
public class ChangeLogServiceImpl implements ChangeLogService {

    private final ChangeLogRepository changeLogRepository;
    private final ChangeLogDiffRepository changeLogDiffRepository;

    @Override
    public void createRecord(Employee afterValue, String memo, String ipAddress) {
        handleRecord(null, afterValue, memo, ipAddress, ChangeType.CREATED);
    }

    @Override
    public void updateRecord(Employee beforeValue, Employee afterValue, String memo, String ipAddress) {
        handleRecord(beforeValue, afterValue, memo, ipAddress, ChangeType.UPDATED);
    }

    @Override
    public void deleteRecord(Employee beforeValue, String ipAddress) {
        handleRecord(beforeValue, null, null, ipAddress, ChangeType.DELETED);
    }

    @Override
    public CursorPageResponseChangeLogDto getChangeLogs(ChangeLogSearchCondition condition) {
        return changeLogRepository.searchChangeLogs(condition);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiffDto> getChangeLogDiffs(Long changeLogId) {
        ChangeLog changeLog = changeLogRepository.findById(changeLogId)
            .orElseThrow(() -> new EntityNotFoundException("변경 이력을 찾을 수 없습니다"));

        return changeLog.getDiffs().stream()
            .map(diff -> new DiffDto(diff.getPropertyName(), diff.getBefore(), diff.getAfter()))
            .collect(Collectors.toList());
    }

    @Override
    public long countAllChangeLogs(Instant from, Instant to) {
        return changeLogRepository.countByAtBetween(from, to);
    }


    private void handleRecord(Employee beforeValue, Employee afterValue, String memo, String ipAddress, ChangeType type) {
        String effectiveMemo = resolveMemo(memo, type);
        recordChangeLog(beforeValue, afterValue, effectiveMemo, ipAddress, type);
    }

    // 메모 기본 값 보정
    private String resolveMemo(String memo, ChangeType changeType) {
        if (memo != null && !memo.isBlank()) {
            return memo;
        }

        return switch (changeType) {
            case CREATED -> "신규 직원 등록";
            case UPDATED -> "직원 정보 수정";
            case DELETED -> "직원 삭제";
        };
    }

    // 이력 필드 저장
    private void recordChangeLog(Employee beforeValue, Employee afterValue, String memo, String ipAddress, ChangeType changeType) {

        if (changeType == null) {
            throw new IllegalArgumentException("changeType은 필수입니다");
        }

        if (changeType == ChangeType.CREATED && afterValue == null) {
            throw new IllegalArgumentException("CREATED 타입에서는 afterValue가 필수입니다");
        }

        if ((changeType == ChangeType.UPDATED || changeType == ChangeType.DELETED) && beforeValue == null) {
            throw new IllegalArgumentException("UPDATED/DELETED 타입에서는 beforeValue가 필수입니다");
        }

        List<ChangeLogDiff> diffs = new ArrayList<>();

        switch (changeType) {
            case CREATED:
                // 생성 시 beforeValue 부재, 필수 afterValue로 채우기
                diffs.add(createDiff(PropertyName.NAME, null, afterValue.getName()));
                diffs.add(createDiff(PropertyName.EMAIL, null, afterValue.getEmail()));
                diffs.add(createDiff(PropertyName.DEPARTMENT, null, afterValue.getDepartment() != null ? afterValue.getDepartment().getName() : null));
                diffs.add(createDiff(PropertyName.POSITION, null, afterValue.getPosition()));
                diffs.add(createDiff(PropertyName.HIRE_DATE, null, afterValue.getHireDate().toString()));
                diffs.add(createDiff(PropertyName.EMPLOYEE_NUMBER, null, afterValue.getEmployeeNumber()));
                diffs.add(createDiff(PropertyName.STATUS, null, afterValue.getStatus() != null ? afterValue.getStatus().name() : null));
                break;

            case UPDATED:
                // 직원 정보 수정 필드 감지
                if (!Objects.equals(beforeValue.getName(), afterValue.getName())) {
                    diffs.add(createDiff(PropertyName.NAME,  beforeValue.getName(), afterValue.getName()));
                }

                if (!Objects.equals(beforeValue.getEmail(), afterValue.getEmail())) {
                    diffs.add(createDiff(PropertyName.EMAIL,  beforeValue.getEmail(), afterValue.getEmail()));
                }

                if (!Objects.equals(beforeValue.getDepartment().getName(), afterValue.getDepartment().getName())) {
                    diffs.add(createDiff(PropertyName.DEPARTMENT, beforeValue.getDepartment().getName(), afterValue.getDepartment().getName()));
                }

                if (!Objects.equals(beforeValue.getPosition(), afterValue.getPosition())) {
                    diffs.add(createDiff(PropertyName.POSITION, beforeValue.getPosition(), afterValue.getPosition()));
                }

                if (!Objects.equals(beforeValue.getHireDate(), afterValue.getHireDate())) {
                    diffs.add(createDiff(PropertyName.HIRE_DATE, beforeValue.getHireDate().toString(), afterValue.getHireDate().toString()));
                }

                if (!Objects.equals(beforeValue.getStatus(), afterValue.getStatus())) {
                    diffs.add(createDiff(PropertyName.STATUS, beforeValue.getStatus().name(), afterValue.getStatus().name()));
                }
                break;

            case DELETED:
                // 모든 afterValue null 변환
                diffs.add(createDiff(PropertyName.NAME, beforeValue.getName(), null));
                diffs.add(createDiff(PropertyName.EMAIL, beforeValue.getEmail(), null));
                diffs.add(createDiff(PropertyName.DEPARTMENT, beforeValue.getDepartment() != null ? beforeValue.getDepartment().getName() : null, null));
                diffs.add(createDiff(PropertyName.POSITION, beforeValue.getPosition(), null));
                diffs.add(createDiff(PropertyName.HIRE_DATE, beforeValue.getHireDate().toString(), null));
                diffs.add(createDiff(PropertyName.STATUS, beforeValue.getStatus() != null ? beforeValue.getStatus().name() : null, null));
                break;
        }

        // 직원 이력 저장
        if (!diffs.isEmpty()) {
            ChangeLog changeLog = ChangeLog.builder()
                .type(changeType)
                // CREATED 시 afterValue 사용, UPDATED & DELETED 시 beforeValue 사용
                .employeeNumber(changeType ==  ChangeType.CREATED ? afterValue.getEmployeeNumber() : beforeValue.getEmployeeNumber())
                .memo(memo)
                .ipAddress(ipAddress)
                .at(Instant.now())
                .build();

            changeLogRepository.save(changeLog);

            for(ChangeLogDiff diff : diffs) {
                diff.update(changeLog);
                changeLogDiffRepository.save(diff);
            }
        }
    }

    // 변경 상세 내용 필드 구성
    private ChangeLogDiff createDiff(PropertyName property, String beforeValue, String afterValue) {
        return ChangeLogDiff.builder()
            .propertyName(property.getLabel())
            .before(beforeValue)
            .after(afterValue)
            .build();
    }
}
