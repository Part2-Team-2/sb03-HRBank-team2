package org.yebigun.hrbank.domain.binaryContent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.yebigun.hrbank.domain.backup.dto.BackupDto;
import org.yebigun.hrbank.domain.binaryContent.dto.BinaryContentResponseDto;
import org.yebigun.hrbank.domain.binaryContent.entity.BinaryContent;
import org.yebigun.hrbank.domain.binaryContent.repository.BinaryContentRepository;
import org.yebigun.hrbank.domain.binaryContent.service.BinaryContentService;
import org.yebigun.hrbank.domain.binaryContent.storage.BinaryContentStorage;
import org.yebigun.hrbank.global.dto.ErrorResponse;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.controller
 * FileName     : BinaryContentController
 * Author       : dounguk
 * Date         : 2025. 6. 5.
 */
@Tag(name = "파일 관리", description = "파일 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/files")
public class BinaryContentController {
    private final BinaryContentService binaryContentService;
    private final BinaryContentStorage binaryContentStorage;
    private final BinaryContentRepository binaryContentRepository;

    // id = binary content id
    @Operation(summary = "파일 다운로드")
    @ApiResponses({
        @ApiResponse(
            responseCode = "200",
            description = "다운로드 성공",
            content = @Content(
                mediaType = "*/*",
                examples = @ExampleObject(
                    value = "string"
                )
            )
        ),
        @ApiResponse(
            responseCode = "404",
            description = "파일을 찾을 수 없음",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        ),
        @ApiResponse(
            responseCode = "500",
            description = "서버 오류",
            content = @Content(
                mediaType = "*/*",
                schema = @Schema(implementation = ErrorResponse.class)
            )
        )
    })
    @GetMapping("{id}/download")
    public ResponseEntity<?> downloadBinaryContent(@PathVariable Long id) {

        BinaryContentResponseDto binaryContentResponseDto = binaryContentService.find(id);
        return binaryContentStorage.download(binaryContentResponseDto);
    }

    // 삭제 예정입니다. test only
    @Operation(summary = "🚨삭제예정 (파일 업로드)")
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
