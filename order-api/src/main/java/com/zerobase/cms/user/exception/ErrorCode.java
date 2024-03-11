package com.zerobase.cms.user.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {

    NOT_FOUND_USER(HttpStatus.BAD_REQUEST, "일치하는 회원이 없습니다");

    private final HttpStatus httpStatus;
    private final String detail;
}
