package com.innowise.userservice.exception;

import com.innowise.userservice.model.dto.ApiErrorResponse;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ApiErrorResponse> handleNotFound(EntityNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(new ApiErrorResponse(exception.getMessage()));
  }

  @ExceptionHandler(BusinessValidationException.class)
  public ResponseEntity<ApiErrorResponse> handleBusinessValidation(BusinessValidationException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiErrorResponse(exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult()
        .getFieldErrors()
        .stream()
        .map(GlobalExceptionHandler::formatFieldError)
        .collect(Collectors.joining("; "));

    if (message.isBlank()) {
      message = "Validation failed";
    }

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(new ApiErrorResponse(message));
  }
  
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiErrorResponse> handleAny(Exception exception) {
    log.error("Unexpected error", exception);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .body(new ApiErrorResponse("Unexpected error"));
  }

  private static String formatFieldError(FieldError error) {
    String field = error.getField();
    String defaultMessage = error.getDefaultMessage();
    return field + ": " + defaultMessage;
  }

}
