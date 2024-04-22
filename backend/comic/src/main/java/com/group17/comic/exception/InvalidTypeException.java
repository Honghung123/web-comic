package com.group17.comic.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class InvalidTypeException extends RuntimeException implements CustomException{
    private HttpStatus statusCode;

    public InvalidTypeException(String message) {
        super(message);
        this.statusCode = HttpStatus.BAD_REQUEST;
    }
}
