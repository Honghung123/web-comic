package com.group17.comic.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException{
    private String message;
    private HttpStatus status;
    public CustomException(HttpStatus status, String message) {
        super(message);
        this.message = message;
        this.status = status;
    }   
} 
