package org.yebigun.hrbank.domain.binaryContent.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponse;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.repository.BinaryContentRepository;
import org.yebigun.hrbank.domain.binaryContent.service.BinaryContentService;
import org.yebigun.hrbank.domain.binaryContent.storage.BinaryContentStorage;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.controller
 * FileName     : BinaryContentController
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("api/binaryContents")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;

    // id = binary content id
    @GetMapping("{id}/download")
    public ResponseEntity<?> downloadBinaryContent(@PathVariable Long id) {
        BinaryContentResponse binaryContentResponse = binaryContentService.find(id);
        return binaryContentStorage.download(binaryContentResponse);
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadTest(@RequestParam("file") MultipartFile file) {
        try {
            BinaryContent binaryContent = BinaryContent.builder()
                .fileName(file.getOriginalFilename())
                .size(file.getSize())
                .contentType(file.getContentType())
                .build();
            binaryContentRepository.save(binaryContent);
            binaryContentStorage.put(binaryContent.getId(), file.getBytes());
            return ResponseEntity.ok(binaryContent.getId().toString());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("파일 저장 실패: " + e.getMessage());
        }
    }

}
