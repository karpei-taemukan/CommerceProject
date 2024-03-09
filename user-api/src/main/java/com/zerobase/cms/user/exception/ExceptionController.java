package com.zerobase.cms.user.exception;

import jakarta.servlet.ServletException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class ExceptionController {
    @ExceptionHandler({CustomException.class})
    public ResponseEntity<ExceptionResponse> customRequestException(final CustomException e){
        log.warn("api Exception: "+e.getErrorCode());
        return ResponseEntity.badRequest().body(new ExceptionResponse(e.getMessage(), e.getErrorCode()));
    }

    @ExceptionHandler({ServletException.class})
    public ResponseEntity<String> securityException(final CustomException e){
        log.warn("api Exception: "+e.getErrorCode());
        return ResponseEntity.badRequest().body("잘못된 인증 시도 입니다");
    }


    @Getter
    @ToString
    @AllArgsConstructor
    public static class ExceptionResponse {
        private String message;
        private ErrorCode errorCode;
    }
}