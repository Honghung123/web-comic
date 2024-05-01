package com.group17.comic.exception.customs;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class InvalidPluginListException extends RuntimeException {
      private HttpStatus status;

    public InvalidPluginListException(String message, HttpStatus status) {
        super(message); 
        this.status = status;
    }
}
