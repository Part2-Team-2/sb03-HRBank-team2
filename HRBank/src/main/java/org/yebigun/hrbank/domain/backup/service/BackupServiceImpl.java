package org.yebigun.hrbank.domain.backup.service;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.backup.Temporary.TempEmployeeDto;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.dto.CursorPageResponseBackupDto;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.domain.backup.entity.QBackup;
import org.yebigun.hrbank.domain.backup.mapper.BackupMapper;
import org.yebigun.hrbank.domain.backup.repository.BackupRepository;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.storage.BinaryContentStorage;
import org.yebigun.hrbank.domain.employee.entity.Employee;
import org.yebigun.hrbank.domain.employee.repository.EmployeeRepository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

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
    private static final String STATUS = "status";



    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;
    private final BinaryContentStorage binaryContentStorage;
    private final EmployeeRepository employeeRepository;
    private final JPAQueryFactory queryFactory;


    @Transactional(readOnly = true)
    @Override
    public CursorPageResponseBackupDto findAsACursor(
        String worker
        , String status
        , Instant startedAtFrom
        , Instant startedAtTo
        , Long idAfter // 백업 id index 231
        , Instant cursor // Instant 마지막 요소의 날짜
        , int size // 30
        , String sortField // "startedAt", "endedAt", "status"
        , String sortDirection // DESC, ASC
    ) {
        long totalElements = backupRepository.count();
        List<Backup> backups = getRequestedBackups(
            worker, status, startedAtFrom, startedAtTo, idAfter, cursor, size, sortField, sortDirection);

//        List<BackupDto> backupDtos = backups.stream().map(backupMapper::toDto).toList();


        boolean hasNext = backups.size() > size;
        Instant nextCursor = null;
        long nextIdAfter = 0;
        if(hasNext) {
            backups = backups.subList(0, size);
            nextCursor = backups.get(backups.size() - 1).getStartedAtFrom();
            nextIdAfter = backups.get(backups.size() - 1).getId();
        }

        List<BackupDto> backupDtos = backups.stream().map(backupMapper::toDto).toList();

        CursorPageResponseBackupDto response = CursorPageResponseBackupDto.builder()
            .content(backupDtos)
            .nextCursor(nextCursor)
            .hasNext(hasNext)
            .nextIdAfter(nextIdAfter)
            .totalElements(totalElements)
            .build();
        return response;
    }

    private List<Backup> getRequestedBackups(
        String worker, String status, Instant startedAtFrom, Instant startedAtTo, Long idAfter, Instant cursor, int size, String sortField, String sortDirection) {

        QBackup qBackup = QBackup.backup;
        BooleanBuilder where = new BooleanBuilder();
        if(worker != null) {
            where.and(qBackup.employeeIp.contains(worker));
        }
        if(status != null) {
            where.and(qBackup.backupStatus.eq(BackupStatus.valueOf(status)));
        }
        if(startedAtFrom != null) {
            where.and(qBackup.createdAt.goe(startedAtFrom));
        }
        if(startedAtTo != null) {
            where.and(qBackup.createdAt.loe(startedAtTo));
        }

        Order orderBy = sortDirection.equalsIgnoreCase("ASC") ? Order.ASC : Order.DESC;

        // started at 기준 오름차순
        if (cursor != null) {
            if (STARTED_AT.equals(sortField)) {
                where.and(orderBy == Order.ASC
                    ? qBackup.startedAtFrom.gt(cursor) : qBackup.startedAtFrom.lt(cursor));
            } else if (ENDED_AT.equals(sortField)) {
                where.and(orderBy == Order.ASC
                    ? qBackup.startedAtTo.gt(cursor) : qBackup.startedAtTo.lt(cursor));
            } else if (STATUS.equals(sortField)) {
                where.and(orderBy == Order.ASC
                    ? qBackup.backupStatus.gt(BackupStatus.valueOf(status)) :
                    qBackup.backupStatus.lt(BackupStatus.valueOf(status))
                );
            }
        }

        List<Backup> backups = queryFactory
            .selectFrom(qBackup)
            .where(where)
            .orderBy(getOrderSpecifier(sortField, orderBy, qBackup))
            .limit(size)
            .fetch();

        return backups;
    }

    private OrderSpecifier<?> getOrderSpecifier(String sortField, Order orderBy, QBackup backup) {
        return switch (sortField) {
            case "startedAt" -> new OrderSpecifier<>(orderBy, backup.startedAtFrom);
            case "endedAt" -> new OrderSpecifier<>(orderBy, backup.startedAtTo);
            case "status" -> new OrderSpecifier<>(orderBy, backup.backupStatus);
            default -> new OrderSpecifier<>(orderBy, backup.id);
        };
    }




    @Transactional
    @Override
    public BackupDto createBackup(HttpServletRequest request) {
        Backup.BackupBuilder backupBuilder = Backup.builder()
            .startedAtFrom(Instant.now())
            .employeeIp(getIp(request));

        // 변경 감지 : 가장 최근 완료된 배치 작업 시간 이후 직원 데이터가 변경된 경우에 데이터 백업이 필요한 것으로 간주합니다.
        boolean change = true;

        if (!change) {
            log.warn("변경사항 없음");
            return processSkippedBackup(backupBuilder);
        }

        log.warn("변경사항 있음");
        try{
            log.warn("변경사항 저장");
            return processCompletedBackup(backupBuilder);
        } catch (Exception e) {
            log.warn("변경사항 실패");
            return processFailedBackup(backupBuilder,e);
        }

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
        List<TempEmployeeDto> employees = toDto(employeeRepository.findAll());

        BinaryContent csvFile = binaryContentStorage.putCsv(employees); // 내부에서 삭제
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
            BinaryContent logFile = binaryContentStorage.putLog(backup.getId(), exception);
            backup.addLogFile(logFile);
            return backupMapper.toDto(backup);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if(ip !=null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
            ip=ip.split(",")[0].trim();
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
            if(ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
            if(ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            if(ip != null && isValidIp(ip)) return ip;
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    private boolean isValidIp(String ip) {
        if (ip == null || ip.trim().isEmpty()) return false;

        try{
            java.net.InetAddress.getByName(ip);
            return true;
        } catch (Exception e){
            return false;
        }
    }

    private List<TempEmployeeDto> toDto(List<Employee> employees) {
        List<TempEmployeeDto> dtoList = new ArrayList<>();

        for (Employee employee : employees) {
            TempEmployeeDto employeeDto = TempEmployeeDto.builder()
                .id(employee.getId())
                .employeeNumber(employee.getEmployeeNumber())
                .name(employee.getName())
                .email(employee.getEmail())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .position(employee.getPosition())
                .hireDate(employee.getHireDate())
                .status(employee.getStatus())
                .build();
            dtoList.add(employeeDto);
        }

        return dtoList;
    }

}
