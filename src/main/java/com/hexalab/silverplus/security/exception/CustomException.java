package com.hexalab.silverplus.security.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class CustomException extends AuthenticationException {
    private final HttpStatus httpStatus; // HTTP 상태 코드
    private final String errorCode;     // 예외 유형 코드

    public CustomException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = null; // 기본값
    }

    public CustomException(String message, HttpStatus httpStatus, String errorCode) {
        super(message);
        this.httpStatus = httpStatus;
        this.errorCode = errorCode;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public String getErrorCode() {
        return errorCode;
    }

}
