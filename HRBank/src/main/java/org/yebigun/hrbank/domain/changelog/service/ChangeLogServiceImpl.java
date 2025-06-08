package org.yebigun.hrbank.domain.changelog.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLog;
import org.yebigun.hrbank.domain.changelog.entity.ChangeLogDiff;
import org.yebigun.hrbank.domain.changelog.entity.ChangeType;
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
    public void recordChangeLog(Employee beforeValue, Employee afterValue, String memo, String ipAddress, ChangeType changeType) {

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
                diffs.add(createDiff("이름", null, afterValue.getName()));
                diffs.add(createDiff("이메일", null, afterValue.getEmail()));
                diffs.add(createDiff("부서명", null, afterValue.getDepartment() != null ? afterValue.getDepartment().getName() : null));
                diffs.add(createDiff("직함", null, afterValue.getPosition()));
                diffs.add(createDiff("입사일", null, afterValue.getHireDate().toString()));
                diffs.add(createDiff("사번", null, afterValue.getEmployeeNumber()));
                diffs.add(createDiff("상태", null, afterValue.getStatus() != null ? afterValue.getStatus().name() : null));
                break;

            case UPDATED:
                // 직원 정보 수정 필드 감지
                if (!Objects.equals(beforeValue.getName(), afterValue.getName())) {
                    diffs.add(createDiff("이름",  beforeValue.getName(), afterValue.getName()));
                }

                if (!Objects.equals(beforeValue.getEmail(), afterValue.getEmail())) {
                    diffs.add(createDiff("이메일",  beforeValue.getEmail(), afterValue.getEmail()));
                }

                if (!Objects.equals(beforeValue.getDepartment().getName(), afterValue.getDepartment().getName())) {
                    diffs.add(createDiff("부서명", beforeValue.getDepartment().getName(), afterValue.getDepartment().getName()));
                }

                if (!Objects.equals(beforeValue.getPosition(), afterValue.getPosition())) {
                    diffs.add(createDiff("직함", beforeValue.getPosition(), afterValue.getPosition()));
                }

                if (!Objects.equals(beforeValue.getHireDate(), afterValue.getHireDate())) {
                    diffs.add(createDiff("입사일", beforeValue.getHireDate().toString(), afterValue.getHireDate().toString()));
                }

                if (!Objects.equals(beforeValue.getEmployeeNumber(), afterValue.getEmployeeNumber())) {
                    diffs.add(createDiff("사번", beforeValue.getEmployeeNumber(), afterValue.getEmployeeNumber()));
                }

                if (!Objects.equals(beforeValue.getStatus(), afterValue.getStatus())) {
                    diffs.add(createDiff("상태", beforeValue.getStatus().name(), afterValue.getStatus().name()));
                }
                break;

            case DELETED:
                // 모든 afterValue null 변환
                diffs.add(createDiff("이름", beforeValue.getName(), null));
                diffs.add(createDiff("이메일", beforeValue.getEmail(), null));
                diffs.add(createDiff("부서명", beforeValue.getDepartment() != null ? beforeValue.getDepartment().getName() : null, null));
                diffs.add(createDiff("직함", beforeValue.getPosition(), null));
                diffs.add(createDiff("입사일", beforeValue.getHireDate().toString(), null));
                diffs.add(createDiff("사번", beforeValue.getEmployeeNumber(), null));
                diffs.add(createDiff("상태", beforeValue.getStatus() != null ? beforeValue.getStatus().name() : null, null));
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
                changeLogDiffRepository.save(
                    diff.toBuilder()
                        .changeLog(changeLog)
                        .build());
            }
        }
    }

    // 변경 상세 내용 필드 구성
    private ChangeLogDiff createDiff(String propertyName, String beforeValue, String afterValue) {
        return ChangeLogDiff.builder()
            .propertyName(propertyName)
            .before(beforeValue)
            .after(afterValue)
            .build();
    }
}
