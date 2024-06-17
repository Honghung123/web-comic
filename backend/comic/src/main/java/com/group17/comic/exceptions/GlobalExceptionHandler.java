package com.group17.comic.exceptions;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.ConstraintViolationException;

import org.jsoup.HttpStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.group17.comic.dtos.response.ErrorResponse;
import com.group17.comic.log.Logger;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleConstraintValidationException(BindException ex, WebRequest request) {
        List<ObjectError> listErrors = ex.getBindingResult().getAllErrors();
        String message = listErrors.get(0).getDefaultMessage();
        Logger.logError(message, ex);
        var httpStatus = HttpStatus.BAD_REQUEST;
        String error = httpStatus.getReasonPhrase();
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }

    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse> handleValidationException(
            ConstraintViolationException ex, WebRequest request) {
        Logger.logError(ex.getMessage(), ex);
        String message = ex.getMessage().substring(ex.getMessage().lastIndexOf(": ") + 2);
        var httpStatus = HttpStatus.BAD_REQUEST;
        String error = httpStatus.getReasonPhrase();
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }

    @ExceptionHandler({HttpStatusException.class})
    public ResponseEntity<ErrorResponse> handleHttpStatusException(HttpStatusException ex, WebRequest request) {
        Logger.logError(ex.getMessage(), ex);
        String message = ex.getMessage();
        var httpStatus = HttpStatus.NOT_FOUND;
        String error = httpStatus.getReasonPhrase();
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<ErrorResponse> handleCustomException(BusinessException ex, WebRequest request) {
        Logger.logError(ex.getMessage(), null);
        String message = ex.getMessage();
        var httpStatus = ex.getStatus();
        String error = httpStatus.getReasonPhrase();
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception ex, WebRequest request) {
        Logger.logError(ex.getMessage(), ex);
        var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
        String error = httpStatus.getReasonPhrase();
        String message = "An error has occured on server.";
        LocalDateTime timestamp = LocalDateTime.now();
        String path = request.getDescription(false).replace("uri=", "");
        return ResponseEntity.status(httpStatus)
                .body(new ErrorResponse(httpStatus.value(), error, message, timestamp, path));
    }
}
