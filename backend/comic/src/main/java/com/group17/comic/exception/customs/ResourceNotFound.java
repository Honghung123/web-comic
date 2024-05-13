package com.group17.comic.exception.customs;
import org.springframework.http.HttpStatus;
import com.group17.comic.exception.CustomException;
 

public class ResourceNotFound extends CustomException {
    public ResourceNotFound(String message) {
        super(HttpStatus.NOT_FOUND, message); 
    }    
}
