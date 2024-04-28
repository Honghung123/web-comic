package com.group17.comic.exception.customs;
import org.springframework.http.HttpStatus;
import com.group17.comic.exception.CustomException;
import lombok.Getter; 

@Getter
public class ResourceNotFound extends RuntimeException implements CustomException {
    private HttpStatus status; 
    public ResourceNotFound(String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }    
}
