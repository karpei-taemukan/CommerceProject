package com.zerobase.cms.order.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiException {

    @ExceptionHandler({CustomException.class})
    public ResponseEntity<CustomException.CustomExceptionResponse> exceptionHandler(
            HttpServletRequest request,
            final CustomException e
    ){
        return ResponseEntity
                .status(e.getStatus())
                .body(CustomException.CustomExceptionResponse.builder()
                        .code(e.getErrorCode().name())
                        .message(e.getMessage())
                        .status(e.getStatus())
                        .build());
    }
}
