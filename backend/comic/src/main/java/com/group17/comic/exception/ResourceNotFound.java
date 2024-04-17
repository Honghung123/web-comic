package com.group17.comic.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ResourceNotFound extends RuntimeException {
    private HttpStatus statusCode; 
    public ResourceNotFound(HttpStatus statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }    
}
