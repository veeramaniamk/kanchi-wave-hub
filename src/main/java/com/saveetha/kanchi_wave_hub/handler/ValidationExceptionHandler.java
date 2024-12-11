package com.saveetha.kanchi_wave_hub.handler;

import java.security.SignatureException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.saveetha.kanchi_wave_hub.response.ApiResponse;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                                .getFieldErrors()
                                .stream()
                                .map(error -> error.getDefaultMessage())
                                .findFirst() // Pick the first validation error
                                .orElse("Validation error");

        ApiResponse response = new ApiResponse(400, errorMessage); // 400 for Bad Request
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(ConstraintViolationException ex) {
        StringBuilder message = new StringBuilder();
        
        // Aggregate all error messages into a single line
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            message.append(violation.getMessage());
            break;
        }
        
        // Create custom response
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", message.toString().trim());
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    public ResponseEntity<Map<String, Object>> missingHeader(MissingRequestHeaderException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", "Cannot Authenticate");
        
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, Object>> resourceNotFound(NoResourceFoundException ex) {
        // Create custom response
        Map<String, Object> response = new HashMap<>();
        response.put("status", 404);
        response.put("message", ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<Map<String, Object>> tokenError(SignatureException ex) {
        // Create custom response
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> runtime(RuntimeException ex) {
        // Create custom response
        Map<String, Object> response = new HashMap<>();
        response.put("status", 400);
        response.put("message", ex.getMessage());
        
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> violaException(SQLException ex) {
        // Customize your error message
        String errorMessage = "DataIntegrityViolationException code " + ex.getErrorCode()+ " msg "+ex.getMessage();
        return new ResponseEntity<>(new ApiResponse(400, errorMessage), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse> handleSQLException(SQLException ex) {
        // Customize your error message
        String errorMessage = ex.getMessage();
        return new ResponseEntity<>(new ApiResponse(500, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
