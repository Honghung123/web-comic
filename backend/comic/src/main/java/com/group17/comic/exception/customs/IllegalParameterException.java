package com.group17.comic.exception.customs;

import org.springframework.http.HttpStatus;

import com.group17.comic.exception.CustomException;

public class IllegalParameterException extends CustomException {
      public IllegalParameterException(String message) {
          super(HttpStatus.BAD_REQUEST, message);
      }
}
