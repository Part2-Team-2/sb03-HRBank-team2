package org.yebigun.hrbank.domain.binaryContent.storage;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponse;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.repository.BinaryContentRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.storage
 * FileName     : BinaryContentStorageImpl
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */

@Transactional
@Service
@RequiredArgsConstructor
public class BinaryContentStorageImpl implements BinaryContentStorage {
        private static final String PROFILE_PATH = "uploads";
        private final BinaryContentRepository binaryContentRepository;

        private Path root;

        @Value("${file.upload.all.path}")
        private String path;


        @PostConstruct
        public void init() {
            String uploadPath = new File(path).getAbsolutePath() + "/" + PROFILE_PATH;
            File directory = new File(uploadPath);

            if (!directory.exists() && !directory.mkdirs()) {
                throw new RuntimeException(uploadPath);
            }
            this.root = Paths.get(uploadPath);
        }


        @Override
        public Long put(Long binaryContentId, byte[] bytes) {
            BinaryContent attachment = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information is not saved"));
            String extention = getExtention(attachment.getFileName());
            Path path = resolvePath(binaryContentId, extention);

            try (FileOutputStream fos = new FileOutputStream(path.toFile())) {
                fos.write(bytes);
            } catch (IOException e) {
                throw new RuntimeException("image not saved", e);
            }
            return attachment.getId();
        }

        @Override
        public InputStream get(Long binaryContentId) {
            BinaryContent binaryContent = binaryContentRepository.findById(binaryContentId).orElseThrow(() -> new IllegalStateException("image information not found"));
            String extention = getExtention(binaryContent.getFileName());

            Path path = resolvePath(binaryContentId, extention);

            if (!Files.exists(path)) {
                throw new RuntimeException("file not found: " + path);
            }

            try {
                return Files.newInputStream(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public ResponseEntity<?> download(BinaryContentResponse response) {
            try {
                byte[] bytes = get(response.id()).readAllBytes();
                return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(response.contentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+response.fileName()+"\"")
                    .contentLength(response.size())
                    .body(bytes);

            } catch (IOException e) {
                throw new RuntimeException("파일 다운 실패"+response.fileName()+" "+e);
            }
        }

        private Path resolvePath(Long id, String extention) {
            return root.resolve(id.toString()+ extention);
        }

        private String getExtention(String fileName) {
            int index = fileName.lastIndexOf(".");
            if (index == -1 || index == fileName.length() - 1) {
                return "";
            }
            return fileName.substring(index);
        }
}
