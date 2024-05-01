package com.group17.comic.exception.customs;
import org.springframework.http.HttpStatus;
import com.group17.comic.exception.CustomException;
import lombok.Getter;

@Getter
public class InvalidTypeException extends CustomException{
    public InvalidTypeException(String message) {
        super(HttpStatus.BAD_REQUEST, message); 
    }
}
