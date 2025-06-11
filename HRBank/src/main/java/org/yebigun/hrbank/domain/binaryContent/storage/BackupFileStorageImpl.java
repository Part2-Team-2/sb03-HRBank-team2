package org.yebigun.hrbank.domain.binaryContent.storage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.repository.BinaryContentRepository;
import org.yebigun.hrbank.domain.employee.dto.data.EmployeeDto;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : BackupBinaryContentStorageImpl
 * Author       : dounguk
 * Date         : 2025. 6. 8.
 */
@Log4j2
@Transactional
@Service
@RequiredArgsConstructor
public class BackupFileStorageImpl implements BackupFileStorage {
    private static final String COLUMNS = "ID,직원번호,이름,이메일,부서,직급,입사일,상태";
    private static final String CSV_EXTENSION = ".csv";
    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String LOG_EXTENSION = ".log";
    private static final String LOG_CONTENT_TYPE = "text/plain";
    private static final String PATH = "uploads";

    private final BinaryContentRepository binaryContentRepository;

    private Path root;

    @Value("${file.upload.all.path}")
    private String path;

    @PostConstruct
    public void init() {
        try {
            Path basePath = Paths.get(path).toAbsolutePath().normalize();
            Path uploadPath = basePath.resolve(PATH);

            if (!uploadPath.startsWith(basePath)) {
                throw new SecurityException("경로 조작 시도가 감지되었습니다");
            }
            Files.createDirectories(uploadPath);
            this.root = uploadPath;

        } catch (Exception e) {
            throw new RuntimeException("업로드 디렉토리 생성에 실패했습니다: " + path, e);
        }
    }

    @Override
    public BinaryContent saveCsv(List<EmployeeDto> employees) {
        UUID fileName = UUID.randomUUID();
        Path filePath = root.resolve(fileName + CSV_EXTENSION);

        log.info("파일 생성 시작");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath.toFile(),StandardCharsets.UTF_8))) {
            bw.write(COLUMNS);
            bw.newLine();

            if(employees != null && !employees.isEmpty()) {
                for (EmployeeDto employee : employees) {
                    log.warn("내부 반복");
                    try {
                        bw.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                            employee.id(),
                            employee.employeeNumber(),
                            employee.name(),
                            employee.email(),
                            employee.departmentName() != null ? employee.departmentName() : "",
                            employee.position(),
                            employee.hireDate(),
                            employee.status()
                        ));
                        bw.newLine();
                    } catch (Exception e) {
                        log.warn("직원 정보 쓰기 실패: " + employee.id(), e);
                        try {
                            Files.deleteIfExists(filePath);
                            log.warn("CSV 파일 삭제");
                        } catch (IOException deleteException) {
                            e.addSuppressed(deleteException);
                        }
                        throw new RuntimeException("파일 삭제중 오류 발생");
                    }
                }
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException("파일 생성중 오류 발생", e);
        }

        long fileSize;
        try{
            fileSize = Files.size(filePath);
        } catch (IOException e) {
            throw new RuntimeException("파일 사이즈 계산에 실패했습니다", e);
        }

        BinaryContent binaryContent = BinaryContent.builder()
            .fileName(fileName + CSV_EXTENSION)
            .size(fileSize)
            .contentType(CSV_CONTENT_TYPE)
            .build();
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    @Override
    public BinaryContent saveLog(long backupId, Exception exception) {

        UUID fileName = UUID.randomUUID();
        Path filePath = root.resolve(fileName + LOG_EXTENSION);

        String logMessage = generateLogMessage(backupId, exception);

        try { // log 파일 저장
            Files.write(filePath, logMessage.getBytes(StandardCharsets.UTF_8));
        } catch (IOException deleteException) {
            throw new RuntimeException("로그 저장에 실패했습니다", deleteException);
        }

        long fileSize = getSize(filePath);

        BinaryContent binaryContent = BinaryContent.builder()
            .fileName(fileName + LOG_EXTENSION)
            .size(fileSize)
            .contentType(LOG_CONTENT_TYPE)
            .build();
        binaryContentRepository.save(binaryContent);
        return binaryContent;
    }

    private String generateLogMessage(long backupId, Exception exception) {
        String content =
            """
                [ERROR] 백업 실패
                --------------------------
                백업 ID: %d
                시간: %s
                실패 사유: %s
                --------------------------
                """.formatted(
                backupId,
                Instant.now(),
                exception.getMessage());
        return content;
    }

    private Long getSize(Path tempPath) {
        try{
            return Files.size(tempPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 크기 측정 실패", e);
        }
    }
}
