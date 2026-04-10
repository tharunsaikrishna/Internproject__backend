package com.example.banking_project_application.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<?> handleInvalidRequest(InvalidRequestException ex, HttpServletRequest req) {

        log.warn("InvalidRequestException at path={} message={}", req.getRequestURI(), ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.BAD_REQUEST, req.getRequestURI());
    }


    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<?> handleUnauthorized(UnauthorizedException ex, HttpServletRequest req) {

        log.warn("UnauthorizedException at path={} message={}", req.getRequestURI(), ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.UNAUTHORIZED, req.getRequestURI());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest req) {

        log.error("ResourceNotFoundException at path={} message={}", req.getRequestURI(), ex.getMessage());

        return buildResponse(ex.getMessage(), HttpStatus.NOT_FOUND, req.getRequestURI());
    }

    @ExceptionHandler(AccountBlockedException.class)
    public ResponseEntity<?> handleAccountBlocked(AccountBlockedException ex, HttpServletRequest req) {

        log.warn("AccountBlockedException at path={} message={}", req.getRequestURI(), ex.getMessage());

        return buildResponse(
                ex.getMessage(),
                HttpStatus.FORBIDDEN,
                req.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex, HttpServletRequest req) {

        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> fieldErrors.put(error.getField(), error.getDefaultMessage()));

        log.warn("Validation failed at path={} errors={}", req.getRequestURI(), fieldErrors);

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.BAD_REQUEST.value());
        body.put("error", "Validation Failed");
        body.put("path", req.getRequestURI());
        body.put("details", fieldErrors);

        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest req) {

        log.error("Unhandled exception at path={} message={}", req.getRequestURI(), ex.getMessage(), ex);

        return buildResponse(
                "Something went wrong. Please try again later.",
                HttpStatus.INTERNAL_SERVER_ERROR,
                req.getRequestURI()
        );
    }
    @ExceptionHandler(org.springframework.web.servlet.resource.NoResourceFoundException.class)
    public ResponseEntity<?> handleNoResourceFound(
            org.springframework.web.servlet.resource.NoResourceFoundException ex,
            HttpServletRequest req) {

        log.warn("NoResourceFoundException at path={} message={}", req.getRequestURI(), ex.getMessage());

        return buildResponse(
                "The requested endpoint does not exist: " + req.getRequestURI(),
                HttpStatus.NOT_FOUND,
                req.getRequestURI()
        );
    }
    private ResponseEntity<?> buildResponse(String message, HttpStatus status, String path) {

        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        body.put("path", path);

        return new ResponseEntity<>(body, status);
    }
}