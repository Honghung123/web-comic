package com.group17.comic.exception.customs;

import org.springframework.http.HttpStatus;

import com.group17.comic.exception.CustomException;

import lombok.Getter;

@Getter
public class InvalidPluginListException extends CustomException {
    public InvalidPluginListException(String message) {
        super(HttpStatus.SERVICE_UNAVAILABLE, message);  
    }
}
