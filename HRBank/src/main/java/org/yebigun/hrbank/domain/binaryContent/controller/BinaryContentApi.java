package org.yebigun.hrbank.domain.binaryContent.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.yebigun.hrbank.global.dto.ErrorResponse;

/**
 * PackageName  : org.yebigun.hrbank.domain.binaryContent.controller
 * FileName     : BinaryContentApi
 * Author       : dounguk
 * Date         : 2025. 6. 9.
 */
public interface BinaryContentApi {
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
    ResponseEntity<?> downloadBinaryContent(
        @Parameter(description = "파일 ID", required = true) @PathVariable Long id
    );
}
