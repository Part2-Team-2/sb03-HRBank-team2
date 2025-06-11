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

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.nio.file.*;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
    public int deleteUnusedFiles() {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(root)) {
            // 1. DB에서 사용 중인 파일 이름 목록 수집
            Set<String> validFileNames = binaryContentRepository.findAll()
                .stream()
                .flatMap(b -> {
                    String ext = getExtension(b.getFileName());
                    if (isCsvOrLog(ext)) {
                        return Stream.of(b.getFileName());
                    } else {
                        return Stream.of(b.getId() + ext);
                    }
                })
                .collect(Collectors.toSet());

            int deletedCount = 0;
            for (Path file : stream) {
                if (Files.isRegularFile(file)) {
                    String fileName = file.getFileName().toString();
                    if (!validFileNames.contains(fileName)) {
                        Files.deleteIfExists(file);
                        deletedCount++;
                        log.info("삭제된 미사용 파일: {}", fileName);
                    }
                }
            }
            return deletedCount;

        } catch (IOException e) {
            throw new RuntimeException("미사용 파일 정리에 실패했습니다", e);
        }
    }

    @Override
    public Long put(Long binaryContentId, byte[] bytes) {
        BinaryContent attachment = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new IllegalStateException("image information is not saved"));

        String extension = getExtension(attachment.getFileName());
        Path pathToSave = resolvePathForWrite(attachment, extension);
        Path tempPath = root.resolve(pathToSave.getFileName() + ".tmp");

        try {
            Files.write(tempPath, bytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
            Files.move(tempPath, pathToSave, StandardCopyOption.REPLACE_EXISTING);
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

    @Transactional(readOnly = true)
    @Override
    public InputStream get(Long binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));

        String extension = getExtension(binaryContent.getFileName());
        Path pathToRead = resolvePathForRead(binaryContent, extension);

        if (!Files.exists(pathToRead)) {
            throw new NoSuchElementException("파일을 찾을 수 없습니다.");
        }

        try {
            return Files.newInputStream(pathToRead);
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

    private Path resolvePathForWrite(BinaryContent binaryContent, String extension) {
        String fileName = binaryContent.getFileName();
        if (isCsvOrLog(extension)) {
            return root.resolve(fileName);
        } else {
            return root.resolve(binaryContent.getId() + extension);
        }
    }

    private Path resolvePathForRead(BinaryContent binaryContent, String extension) {
        String fileName = binaryContent.getFileName();
        if (isCsvOrLog(extension)) {
            return root.resolve(fileName);
        } else {
            return root.resolve(binaryContent.getId() + extension);
        }
    }

    boolean isCsvOrLog(String extension) {
        return CSV_EXTENSION.equalsIgnoreCase(extension) || LOG_EXTENSION.equalsIgnoreCase(extension);
    }

    private String getExtension(String fileName) {
        int index = fileName.lastIndexOf(".");
        if (index == -1 || index == fileName.length() - 1) {
            return "";
        }
        return fileName.substring(index).toLowerCase();
    }

    @Override
    public void delete(Long binaryContentId) {
        BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId)
            .orElseThrow(() -> new NoSuchElementException("파일을 찾을 수 없습니다."));

        String extension = getExtension(binaryContent.getFileName());
        Path pathToDelete = resolvePathForRead(binaryContent, extension);

        try {
            Files.deleteIfExists(pathToDelete);
            binaryContentRepository.delete(binaryContent);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패", e);
        }
    }

}
