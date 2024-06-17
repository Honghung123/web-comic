package com.group17.comic.exceptions;

import org.springframework.http.HttpStatus;

import com.group17.comic.enums.ExceptionType;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final String message;
    private final HttpStatus status;

    public BusinessException(ExceptionType ex) {
        super(ex.getMessage());
        this.message = ex.getMessage();
        this.status = HttpStatus.valueOf(ex.getCode());
    }

    public BusinessException(ExceptionType ex, String message) {
        super(ex.getMessage());
        this.message = message;
        this.status = HttpStatus.valueOf(ex.getCode());
    }
}
