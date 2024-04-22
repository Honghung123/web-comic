package com.group17.comic.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter; 

@Getter
public class ResourceNotFound extends RuntimeException implements CustomException {
    private HttpStatus statusCode; 
    public ResourceNotFound(String message) {
        super(message);
        this.statusCode = HttpStatus.BAD_REQUEST;
    }    
}
