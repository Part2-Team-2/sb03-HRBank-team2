package org.yebigun.hrbank.domain.backup.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.backup.entity.Backup;
import org.yebigun.hrbank.domain.backup.entity.BackupStatus;
import org.yebigun.hrbank.domain.backup.mapper.BackupMapper;
import org.yebigun.hrbank.domain.backup.repository.BackupRepository;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;

import java.time.Instant;

/**
 * PackageName  : org.yebigun.hrbank.domain.backup.service
 * FileName     : BackupServiceImpl
 * Author       : dounguk
 * Date         : 2025. 6. 6.
 */

@Transactional
@RequiredArgsConstructor
@Service
public class BackupServiceImpl implements BackupService {
    private final BackupRepository backupRepository;
    private final BackupMapper backupMapper;


    /**
     * 잘못된 요청- 파라미터가 비어있는겅유
     * 이미 진행 중인 백업이 있음
     * 서버 오류
     */
    @Override
    public BackupDto createBackup(HttpServletRequest request) {
        Backup.BackupBuilder backupBuilder = Backup.builder()
            .startedAtFrom(Instant.now())
            .employeeIp(getIp(request));

        // 변경 감지 : 가장 최근 완료된 배치 작업 시간 이후 직원 데이터가 변경된 경우에 데이터 백업이 필요한 것으로 간주합니다.
        boolean change = false;
        // 변경사항 없음
        if (!change) {
            backupBuilder
                .backupStatus(BackupStatus.SKIPPED)
                .binaryContent(null)
                .startedAtTo(Instant.now());

            Backup backup= backupBuilder.build();
            backupRepository.save(backup);
            return backupMapper.toDto(backup);
        }

        // 변경사항 있음
        try{
            // 1. 모든 유저 정보 조회
            // 2. CSV 파일 생성
            BinaryContent sampleCSV = null;

            backupBuilder
                .backupStatus(BackupStatus.COMPLETED)
                .startedAtTo(Instant.now())
                .binaryContent(sampleCSV);
            Backup backup = Backup.builder().build();
            backupRepository.save(backup);
            return backupMapper.toDto(backup);

        } catch (Exception e) {
            // 1. 저장하던 CSV 파일 삭제
            // 2. 실패 로그 생성(.log)
            BinaryContent sampleLog = null;

            backupBuilder
                .backupStatus(BackupStatus.FAILED)
                .startedAtTo(Instant.now())
                .binaryContent(sampleLog);
            Backup backup = Backup.builder().build();
            backupRepository.save(backup);
            return backupMapper.toDto(backup);
        }
    }


    private String getIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
}
