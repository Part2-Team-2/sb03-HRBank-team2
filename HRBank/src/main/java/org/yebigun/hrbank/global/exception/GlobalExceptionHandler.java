package org.yebigun.hrbank.global.exception;

import java.util.NoSuchElementException;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.yebigun.hrbank.global.dto.ErrorResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponse> sendErrorResponse(HttpStatus status, String message,
        String details) {
        return ResponseEntity.status(status).body(ErrorResponse.of(status, message, details));
    }

    // 잘못된 요청을 하는 경우
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleException(IllegalArgumentException e) {

        return sendErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", e.getMessage());
    }

    // 데이터가 존재하지 않을 경우
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleException(NoSuchElementException e) {

        return sendErrorResponse(HttpStatus.NOT_FOUND, "요청한 데이터를 찾을 수 없습니다.", e.getMessage());
    }

    // 실행 도중에 문제가 생겼을 경우
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> runtimeExceptionExceptionHandler(RuntimeException e) {
        return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.",
            e.getMessage());
    }

    // 나머지 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {

        return sendErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다.",
            e.getMessage());
    }

    //유효성 검증 오류 시 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
        MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.joining(", "));
        return sendErrorResponse(HttpStatus.BAD_REQUEST, "잘못된 요청입니다.", message);
    }

}
