package com.group17.comic.exception;

import org.springframework.http.HttpStatus;

public interface CustomException {
    String getMessage();
    HttpStatus getStatus();    
} 
