package org.yebigun.hrbank.domain.binaryContent.storage;

import jakarta.annotation.PostConstruct;
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

import java.io.*;
import java.nio.file.*;
import java.util.NoSuchElementException;

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
    private static final String PATH = "uploads";
    private static final String CSV_EXTENSION = ".csv";
    private static final String LOG_EXTENSION = ".log";

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
        String extention = getExtension(attachment.getFileName());
        Path path = resolvePath(binaryContentId.toString(), extention);
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

    /**
     * 바이너리 콘텐츠의 InputStream을 반환합니다.
     * @param binaryContentId 바이너리 콘텐츠 ID
     * @return InputStream - 호출자가 반드시 close() 해야 함
     * @throws NoSuchElementException 파일을 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    @Override
    public InputStream get(Long binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));
        String extension = getExtension(binaryContent.getFileName());

        if (extension.equals(CSV_EXTENSION) || extension.equals(LOG_EXTENSION)) {
            Path path = resolvePath(binaryContent.getFileName(), "");
            if (!Files.exists(path)) {
                throw new NoSuchElementException("파일을 찾을 수 없습니다.");
            }
            try {
                return Files.newInputStream(path);
            } catch (IOException e) {
                throw new NoSuchElementException("파일을 찾을 수 없습니다.");
            }
        }

        Path path = resolvePath(binaryContentId.toString(), extension);

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

    private Path resolvePath(String fileName, String extension) {
        return root.resolve(fileName + extension);
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index);
    }
}