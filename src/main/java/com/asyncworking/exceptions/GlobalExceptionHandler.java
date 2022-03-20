package com.asyncworking.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = { UserNotFoundException.class })
    public ResponseEntity<Object> handleResourceNotFoundException(RuntimeException ex) {
        log.error("Resource Not Found: ", ex.getMessage());
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = { UserStatusUnexpectedException.class })
    public ResponseEntity<Object> handleUnexpectedStatusException(RuntimeException ex) {
        log.error("Unexpected Status:", ex.getMessage());
        return new ResponseEntity<Object>(ex.getMessage(), HttpStatus.CONFLICT);
    }
}
