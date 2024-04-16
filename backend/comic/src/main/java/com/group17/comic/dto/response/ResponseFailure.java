package com.group17.comic.dto.response;

import org.springframework.http.HttpStatus;

public class ResponseFailure extends ResponseSuccess<Object>{
    public ResponseFailure(HttpStatus statusCode, String message){
        super(statusCode, message);
    }     
}