package org.yebigun.hrbank.domain.binaryContent.storage;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.repository.BinaryContentRepository;
import org.yebigun.hrbank.domain.employee.entity.Employee;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;

import static org.yebigun.hrbank.domain.binaryContent.entity.QBinaryContent.binaryContent;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : BinaryContentStorageImpl
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
@Log4j2
@Transactional
@Service
@RequiredArgsConstructor
public class BinaryContentStorageImpl implements BinaryContentStorage {
    private static final String COLUMNS = "ID,직원번호,이름,이메일,부서,직급,입사일,상태";
    private static final String CSV_EXTENTION = ".csv";
    private static final String CSV_CONTENT_TYPE = "text/csv";
    private static final String LOG_EXTENTION = ".log";
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
    public Long put(Long binaryContentId, byte[] bytes) {
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information is not saved"));
        String extention = getExtention(attachment.getFileName());
        Path path = resolvePath(binaryContentId, extention);
        Path tempPath = root.resolve(path.getFileName() + ".tmp");

        try {
            // 임시 파일에 먼저 쓰기
            Files.write(tempPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try {
                Files.deleteIfExists(tempPath);
            } catch (IOException deleteException) {
                e.addSuppressed(deleteException);
            }
            throw new RuntimeException("파일 저장에 실패했습니다", e);
        }
        return attachment.getId();
    }

    @Override
    public InputStream get(Long binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));
        String extention = getExtention(binaryContent.getFileName());

        Path path = resolvePath(binaryContentId, extention);

        if (!Files.exists(path)) {
            throw new NoSuchElementException("파일을 찾을 수 없습니다.");
        }

        try {
            return Files.newInputStream(path);
        } catch (IOException e) {
            throw new NoSuchElementException("파일을 찾을 수 없습니다.");
        }
    }

    @Override
    public ResponseEntity<?> download(BinaryContentResponseDto response) {
        try {
            InputStream input = get(response.id());
            InputStreamResource resource = new InputStreamResource(input);

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(response.contentType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + response.fileName() + "\"")
                .contentLength(response.size())
                .body(resource);

        } catch (Exception e) {
            throw new NoSuchElementException("파일을 찾을 수 없습니다.");
        }
    }

    @Override
    public BinaryContent putCsv(List<Employee> employees) {
        UUID tempFileName = UUID.randomUUID();
        Path tempPath = root.resolve(tempFileName + CSV_EXTENTION);

        log.warn("파일 생성 시작");
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(tempPath.toFile()))) {
            log.warn("파일 생성");
            bw.write(COLUMNS);
            bw.newLine();

            if(employees != null || !employees.isEmpty()) {
                for (Employee employee : employees) {
                    log.warn("내부 반복");
                    try {
                        bw.write(String.format("%d,%s,%s,%s,%s,%s,%s,%s",
                            employee.getId(),
                            employee.getEmployeeNumber(),
                            employee.getName(),
                            employee.getEmail(),
//                        employee.getDepartment().getId(),
                            "[employee.getDepartment().getId() <- dto 추가되면 매핑해서 추가]",
                            employee.getPosition(),
                            employee.getHireDate(),
                            employee.getStatus()
                        ));
                        bw.newLine();
                    } catch (Exception e) {
                        log.warn("직원 정보 쓰기 실패: " + employee.getId(), e);
                        try {
                            Files.deleteIfExists(tempPath);
                            log.warn("CSV 파일 삭제");
                        } catch (IOException deleteException) {
                            e.addSuppressed(deleteException);
                        }
                    }
                }
            }
            bw.flush();
        } catch (IOException e) {
            throw new RuntimeException("파일 생성중 오류 발생", e);
        }

        long fileSize;
        try{
            fileSize = Files.size(tempPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 사이즈 계산에 실패했습니다", e);
        }

        BinaryContent binaryContent = BinaryContent.builder()
            .fileName(tempFileName + CSV_EXTENTION)
            .size(fileSize)
            .contentType(CSV_CONTENT_TYPE)
            .build();
        binaryContentRepository.save(binaryContent);

        Path path = resolvePath(binaryContent.getId(), CSV_EXTENTION);
        // 4. 바이너리 컨텐츠 이름으로 파일이름 수정
        try{
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            try{
                Files.deleteIfExists(tempPath);
            } catch (IOException deleteException) {
                e.addSuppressed(deleteException);
            }
            throw new RuntimeException("파일 저장에 실패했습니다", e);
        }
        return binaryContent;
    }

    @Override
    public BinaryContent putLog(long backupId, Exception exception) throws IOException {

        UUID tempFileName = UUID.randomUUID();
        Path tempPath = root.resolve(tempFileName + LOG_EXTENTION);

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

        try {
            Files.write(tempPath, content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException deleteException) {
            exception.addSuppressed(deleteException);
            throw new RuntimeException("로그 저장에 실패했습니다", deleteException);
        }

        long fileSize;
        try{
            fileSize = Files.size(tempPath);
        } catch (IOException e) {
            throw new RuntimeException("파일 크기 측정 실패", e);
        }

        BinaryContent binaryContent = BinaryContent.builder()
            .fileName(backupId + LOG_EXTENTION)
            .size(fileSize)
            .contentType(LOG_CONTENT_TYPE)
            .build();
        binaryContentRepository.save(binaryContent);


        Path path = resolvePath(binaryContent.getId(), LOG_EXTENTION);

        try{
            Files.move(tempPath, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception deleteException) {
            Files.deleteIfExists(tempPath);
            throw new RuntimeException("로그 저장에 실패했습니다", deleteException);
        }
        return binaryContent;
    }

    private Path resolvePath(Long id, String extention) {
        return root.resolve(id.toString() + extention);
    }

    private String getExtention(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index);
    }
}
