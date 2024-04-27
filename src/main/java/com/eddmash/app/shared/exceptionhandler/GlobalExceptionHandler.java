package com.eddmash.app.shared.exceptionhandler;

import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.eddmash.app.shared.dto.GenericResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<GenericResponse<Map<String, String>>> handleValidationErrors(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = ex.getBindingResult().getFieldErrors()
                .stream()
                .flatMap(field -> {
                    return Stream.of(Map.entry(field.getField(), field.getDefaultMessage()));
                })
                .collect(Collectors.toMap(Map.Entry::getKey, v -> v.getValue(), (a, b) -> {

                    return a + "," + b;
                }));

        return new ResponseEntity<GenericResponse<Map<String, String>>>(GenericResponse.<Map<String, String>>builder()
                .message("Validation error")
                .data(errors)
                .status(HttpStatus.BAD_REQUEST.value())
                .build(),
                new LinkedMultiValueMap<>(),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ DataAccessException.class })
    public ResponseEntity<Object> handleDatabaseExceptions(Exception ex, WebRequest request) {
        log.error(ex.getClass().getName() + ": " + ex.getMessage(), ex);

        String msg = "Something went wrong please contact admin";

        return new ResponseEntity<>(GenericResponse.builder()
                .message(msg)
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .build(),
                new LinkedMultiValueMap<>(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler({ Exception.class })
    public ResponseEntity<Object> handleExceptions(Exception ex, WebRequest request) {
        log.error(ex.getClass().getName() + ": " + ex.getMessage(), ex);

        String msg = ex.getMessage();

        return new ResponseEntity<>(GenericResponse.builder()
                .message(msg)
                .status(HttpStatus.BAD_REQUEST.value())
                .build(),
                new LinkedMultiValueMap<>(),
                HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler({ AccessDeniedException.class })
    public ResponseEntity<Object> handleAccessDeniedExceptions(Exception ex, WebRequest request) {
        log.error(ex.getClass().getName() + ": " + ex.getMessage());

        String msg = ex.getMessage();

        return new ResponseEntity<>(GenericResponse.builder()
                .message(msg)
                .status(HttpStatus.UNAUTHORIZED.value())
                .build(),
                new LinkedMultiValueMap<>(),
                HttpStatus.UNAUTHORIZED);
    }

}
