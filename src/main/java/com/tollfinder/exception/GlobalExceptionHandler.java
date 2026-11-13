
package com.tollfinder.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, Object>> handleValidation(
      MethodArgumentNotValidException ex) {

    String message = ex.getBindingResult()
        .getFieldErrors()
        .stream()
        .findFirst()
        .map(error -> error.getDefaultMessage())
        .orElse("Invalid request");

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        message);
  }

  @ExceptionHandler(RestClientResponseException.class)
  public ResponseEntity<Map<String, Object>> handleMapplsError(
      RestClientResponseException ex) {

    return buildResponse(
        HttpStatus.BAD_GATEWAY,
        "Unable to get route from Mappls API");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleBadRequest(
      IllegalArgumentException ex) {

    return buildResponse(
        HttpStatus.BAD_REQUEST,
        ex.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGeneralError(
      Exception ex) {

    return buildResponse(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Something went wrong. Please try again later.");
  }

  private ResponseEntity<Map<String, Object>> buildResponse(
      HttpStatus status,
      String message) {

    Map<String, Object> response = Map.of(
        "timestamp", LocalDateTime.now().toString(),
        "status", status.value(),
        "error", status.getReasonPhrase(),
        "message", message);

    return ResponseEntity.status(status).body(response);
  }
}
