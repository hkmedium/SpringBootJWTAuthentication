package com.hk.jwtauth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException
            (ResourceNotFoundException resourceNotFound, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                System.currentTimeMillis(),
                resourceNotFound.getMessage(),
                request.getDescription(false),
                HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Object> handleResourceAlreadyExistsException
            (ResourceAlreadyExistsException alreadyExists, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                System.currentTimeMillis(),
                alreadyExists.getMessage(),
                request.getDescription(false),
                HttpStatus.CONFLICT,
                HttpStatus.CONFLICT.value()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<Object> handleRuntimeException(RuntimeException exception, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse(
                System.currentTimeMillis(),
                "Global Exception:: " + exception.getLocalizedMessage(),
                request.getDescription(false),
                HttpStatus.INTERNAL_SERVER_ERROR,
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
