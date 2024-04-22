package com.group17.comic.exception.customs;
import org.springframework.http.HttpStatus;
import com.group17.comic.exception.CustomException;
import lombok.Getter; 

@Getter
public class ResourceNotFound extends RuntimeException implements CustomException {
    private HttpStatus statusCode; 
    public ResourceNotFound(String message) {
        super(message);
        this.statusCode = HttpStatus.BAD_REQUEST;
    }    
}
