package com.emented.tinkoffservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class ExceptionController extends ResponseEntityExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorDTO> handleNotFound(Exception ex) {
        return new ResponseEntity<>(new ErrorDTO(ex.getLocalizedMessage()), HttpStatus.NOT_FOUND);
    }
}
