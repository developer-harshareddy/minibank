package com.practice.minibank.exception;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(CustomerNotFoundException.class)
	public ResponseEntity<ApiError> handleNotFound(CustomerNotFoundException ex) {

		return error(HttpStatus.NOT_FOUND, ex.getMessage(), Map.of());
	}

	@ExceptionHandler(DuplicateEmailException.class)
	public ResponseEntity<ApiError> handleDuplicateEmail(DuplicateEmailException ex) {

		return error(HttpStatus.CONFLICT, ex.getMessage(), Map.of());
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {

		Map<String, String> fieldErrors = new TreeMap<>();

		ex.getBindingResult().getFieldErrors()
				.forEach(fieldError -> fieldErrors.putIfAbsent(fieldError.getField(), fieldError.getDefaultMessage()));

		return error(HttpStatus.BAD_REQUEST, "Request validation failed", fieldErrors);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ApiError> handleMalformedJson(HttpMessageNotReadableException ex) {

		return error(HttpStatus.BAD_REQUEST, "Request body is missing or contains invalid JSON", Map.of());
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {

		return error(HttpStatus.BAD_REQUEST, "Invalid value for parameter: " + ex.getName(), Map.of());
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ApiError> handleUnexpectedException(Exception ex) {

		log.error("Unexpected error while processing API request", ex);

		return error(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", Map.of());
	}

	private ResponseEntity<ApiError> error(HttpStatus status, String message, Map<String, String> fieldErrors) {

		ApiError body = new ApiError(status.value(), status.getReasonPhrase(), message, fieldErrors);

		return ResponseEntity.status(status).body(body);
	}
}