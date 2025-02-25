package com.thesniffers.exception;

import jakarta.validation.ConstraintViolation;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import jakarta.validation.ConstraintViolationException;


import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Used for @RequestBody Validation
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage()));

        return ResponseEntity.badRequest().body(errors);
    }

    // Used for @RequestParam and @PathVariable Validation
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> handleConstraintViolationExceptions(ConstraintViolationException ex) {
        Map<String, String> errors = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        violation -> violation.getPropertyPath().toString(),
                        ConstraintViolation::getMessage
                ));

        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<String> handleCustomerNotFoundException(CustomerNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFoundException(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    // Handles duplicate entries (Unique constraints)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Record already exists.");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // Handles case when in the database baskets have been saved with wrong status
//    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
//    public ResponseEntity<String> handleInvalidDataAccessApiUsageException(InvalidDataAccessApiUsageException ex) {
//        if (ex.getCause() instanceof IllegalArgumentException) {
//            return ResponseEntity
//                    .status(HttpStatus.EXPECTATION_FAILED)
//                    .body("Invalid shopping basket status found in the database. " +
//                            "Expected values: [NEW, PAID, PROCESSED, UNKNOWN]. " +
//                            "Please check your database entries.");
//        }
//        return ResponseEntity
//                .status(HttpStatus.EXPECTATION_FAILED)
//                .body("A database error occurred. Please contact support.");
//    }
}

