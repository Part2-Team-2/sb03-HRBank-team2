package org.yebigun.hrbank.domain.backup.service;

import com.querydsl.core.types.Order;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.backup.aop.SynchronizedExecution;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.domain.backup.mapper.BackupMapper;
import org.yebigun.hrbank.domain.backup.repository.BackupRepository;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.storage.BackupFileStorage;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.mapper.EmployeeMapper;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

import java.net.InetAddress;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.service
 * FileName     : BackupServiceImpl
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */
@Log4j2
@Transactional
@RequiredArgsConstructor
@Service
public class BackupServiceImpl implements BackupService {
    private static final String STARTED_AT = "startedAt";
    private static final String ENDED_AT = "endedAt";

    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;
    private final BackupFileStorage binaryContentStorage;
    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public void createScheduledBackup() throws Exception {
        String hostIp = InetAddress.getLocalHost().getHostAddress();

        Backup.BackupBuilder backupBuilder = Backup.builder()
            .startedAtFrom(Instant.now())
            .employeeIp(hostIp);

        processBackupIfRequired(backupBuilder);
    }

    @SynchronizedExecution
    @Override
    public BackupDto createBackup(HttpServletRequest request) {
        Backup.BackupBuilder backupBuilder = Backup.builder()
            .startedAtFrom(Instant.now())
            .employeeIp(getIp(request));

        return processBackupIfRequired(backupBuilder);
    }

    private boolean isValidStatus(String status) {
        try{
            BackupStatus.valueOf(status.trim().toUpperCase());
            return true;
        } catch(IllegalArgumentException e){
            return false;
        }
    }

    @Override
    public BackupDto findLatest(String status) {
        if(!isValidStatus(status)) {
            throw new IllegalArgumentException("유효하지 않은 상태값입니다.");
        }

        BackupStatus backupStatus = BackupStatus.valueOf(status.trim().toUpperCase());
        return backupRepository.findTopByBackupStatusOrderByCreatedAtDesc(backupStatus)
            .map(backupMapper::toDto)
            .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseBackupDto findAsACursor(
        String worker, BackupStatus status, Instant startedAtFrom, Instant startedAtTo, Long idAfter, Instant cursor, int size, String sortField, Order sortDirection) {

        if (!sortField.equals(STARTED_AT) && !sortField.equals(ENDED_AT)) {
            throw new IllegalArgumentException("지원하지 않는 정렬 필드히니다");
        }

        // content
        List<Backup> backups = backupRepository.findAllByRequest(
            worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);

        // cursor
        long totalElements = backupRepository.countByRequest(worker, status, startedAtFrom, startedAtTo);
        Instant nextCursor = null;
        long nextIdAfter = 0;
        boolean hasNext = backups.size() == size + 1;

        if (hasNext) {
            backups = backups.subList(0, size);
            Backup lastBackup = backups.get(backups.size() - 1);
            nextIdAfter = lastBackup.getId();

            switch (sortField) {
                case STARTED_AT -> nextCursor = lastBackup.getStartedAtFrom();
                case ENDED_AT -> nextCursor = lastBackup.getStartedAtTo();
                default -> throw new IllegalArgumentException("잘못된 요청 또는 정렬필드");
            }
        }

        // mapping
        List<BackupDto> backupDtos = backups.stream().map(backupMapper::toDto).toList();

        CursorPageResponseBackupDto response = CursorPageResponseBackupDto.builder()
            .content(backupDtos)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .size(size)
            .nextIdAfter(nextIdAfter)
            .totalElements(totalElements)
            .build();
        return response;
    }

    private BackupDto processBackupIfRequired (Backup.BackupBuilder backupBuilder) {
        if (!isBackupRequired()) {
            log.info("변경사항 없음");
            return processSkippedBackup(backupBuilder);
        }
        log.info("변경사항 있음");
        try {
            log.info("변경사항 저장");
            return processCompletedBackup(backupBuilder);
        } catch (Exception e) {
            log.warn("변경사항 실패");
            return processFailedBackup(backupBuilder, e);
        }
    }

    private boolean isBackupRequired() {
        Optional<Instant> lastCreated = employeeRepository.findTopByOrderByCreatedAtDesc().map(Employee::getCreatedAt);
        Optional<Instant> lastUpdated = employeeRepository.findTopByOrderByUpdatedAtDesc().map(Employee::getUpdatedAt);
        Optional<Instant> lastBackedUp = backupRepository.findTopByOrderByCreatedAtDesc().map(Backup::getCreatedAt);

        if (lastBackedUp.isEmpty()) {
            return true;
        }
        Instant backupTime = lastBackedUp.get();

        boolean createdAfterBackup = lastCreated.map(created -> created.isAfter(backupTime)).orElse(false);
        boolean updatedAfterBackup = lastUpdated.map(updated -> updated.isAfter(backupTime)).orElse(false);

        return createdAfterBackup || updatedAfterBackup;
    }


    private BackupDto processSkippedBackup(Backup.BackupBuilder backupBuilder) {
        Backup backup = backupBuilder
            .backupStatus(BackupStatus.SKIPPED)
            .startedAtTo(Instant.now())
            .binaryContent(null)
            .build();

        backupRepository.save(backup);
        return backupMapper.toDto(backup);
    }

    private BackupDto processCompletedBackup(Backup.BackupBuilder backupBuilder) {
        List<EmployeeDto> employees = employeeRepository.findAll().stream().map(employeeMapper::toDto).collect(Collectors.toList());

        BinaryContent csvFile = binaryContentStorage.saveCsv(employees); // 내부에서 삭제
        Backup backup = backupBuilder
            .backupStatus(BackupStatus.COMPLETED)
            .startedAtTo(Instant.now())
            .binaryContent(csvFile)
            .build();

        backupRepository.save(backup);
        return backupMapper.toDto(backup);
    }

    private BackupDto processFailedBackup(Backup.BackupBuilder backupBuilder, Exception exception) {
        Backup backup = backupBuilder
            .backupStatus(BackupStatus.FAILED)
            .startedAtTo(Instant.now())
            .binaryContent(null)
            .build();
        backup = backupRepository.save(backup);

        try {
            BinaryContent logFile = binaryContentStorage.saveLog(backup.getId(), exception);
            backup.addLogFile(logFile);
            return backupMapper.toDto(backup);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            ip = ip.split(",")[0].trim();
            if (isValidIp(ip)) {
                return ip;
            }
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
            if (ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
            if (ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            if (ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            if (ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private boolean isValidIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) return false;

        try {
            java.net.InetAddress.getByName(ip);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
