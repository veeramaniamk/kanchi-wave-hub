package com.saveetha.kanchi_wave_hub.handler;

import java.sql.SQLException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.saveetha.kanchi_wave_hub.response.ApiResponse;

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

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ApiResponse> handleSQLException(SQLException ex) {
        // Customize your error message
        String errorMessage = "A SQL error occurred: " + ex.getMessage();
        return new ResponseEntity<>(new ApiResponse(500, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse> violaException(SQLException ex) {
        // Customize your error message
        String errorMessage = "DataIntegrityViolationException code " + ex.getErrorCode() + " msg "+ ex.getMessage();
        return new ResponseEntity<>(new ApiResponse(500, errorMessage), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
