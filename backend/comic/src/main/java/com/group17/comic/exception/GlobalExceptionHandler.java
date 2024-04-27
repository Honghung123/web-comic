package com.group17.comic.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.group17.comic.dto.response.ErrorResponse;
import com.group17.comic.exception.customs.InvalidTypeException;
import com.group17.comic.exception.customs.ResourceNotFound;
import com.group17.comic.log.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // @ExceptionHandler({ CustomException.class, AnotherCustomException.class })
    // public ResponseEntity<ErrorResponse> handleCustomException(CommonException
    // ex,
    // WebRequest request) {
    // // Use logger here
    // return ResponseEntity.status(ex.getCode()).body(
    // new ErrorResponse(ex.getCode(), ex.getMessage()));
    // }

    // @ExceptionHandler({MethodArgumentNotValidException.class,
    // ConstraintViolationException.class})
    // public ResponseEntity<ErrorResponse> handleValidationException(BindException
    // ex, WebRequest request) {
    // List<ObjectError> listErrors = ex.getBindingResult().getAllErrors();
    // String message = listErrors.get(0).getDefaultMessage();
    // // Use logger here
    // var httpStatus = HttpStatus.BAD_REQUEST;
    // String error = httpStatus.getReasonPhrase();
    // LocalDateTime timestamp = LocalDateTime.now();
    // String path = request.getDescription(false).replace("uri=", "");
    // return ResponseEntity.status(httpStatus).body(
    // new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    // }
    @ExceptionHandler({ ResourceNotFound.class, InvalidTypeException.class })
    public ResponseEntity<ErrorResponse> handleValidationException(CustomException ex, WebRequest request) {
        String message = ex.getMessage();
        var httpStatus = ex.getStatusCode();
        String error = httpStatus.getReasonPhrase();
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        Logger.logError(message, null);
        return ResponseEntity.status(httpStatus).body(
                new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }

    // Catch all Exception.class
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(
            Exception ex, WebRequest request) {
        Logger.logError(ex.getMessage(), ex);
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String error = httpStatus.getReasonPhrase();
        String message = ex.getMessage();
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        return ResponseEntity.status(httpStatus).body(
                new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }
}
