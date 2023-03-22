package com.synchrony.project.exception;


import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.NoSuchElementException;

@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({DataIntegrityViolationException.class, IllegalArgumentException.class})
    public ResponseEntity<Object> handleConstraintViolation(
            Exception ex, WebRequest request) {
        return apiError(HttpStatus.BAD_REQUEST, "Invalid request data", "Unable to process request");
    }

    @ExceptionHandler({NoSuchElementException.class})
    public ResponseEntity<Object> handleNotFound(
            NoSuchElementException ex, WebRequest request) {
        return apiError(HttpStatus.NOT_FOUND, "Requested resource is not found", ex);
    }

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity<Object> handleClientError(HttpClientErrorException ex, WebRequest request) {
        return apiError(HttpStatus.resolve(ex.getStatusCode().value()),
                "Client error encountered from Imgur API", ex);
    }

    @ExceptionHandler({HttpServerErrorException.class})
    public ResponseEntity<Object> handleServerError(HttpClientErrorException ex, WebRequest request) {
        return apiError(HttpStatus.resolve(ex.getStatusCode().value()),
                "Server error encountered from Imgur API", ex);
    }

    @ExceptionHandler({Exception.class})
    public ResponseEntity<Object> handleAll(Exception ex, WebRequest request) {
        return apiError(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error occurred", ex);
    }


    private ResponseEntity<Object> apiError(HttpStatus httpStatus, String errorMessage, Exception ex) {
        logger.error(ex);
        ApiError apiError = new ApiError(
                httpStatus, errorMessage, ex.getMessage());
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }

    private ResponseEntity<Object> apiError(HttpStatus httpStatus, String errorMessage, String error) {
        logger.error(error);
        ApiError apiError = new ApiError(
                httpStatus, errorMessage, error);
        return new ResponseEntity<>(
                apiError, new HttpHeaders(), apiError.getStatus());
    }


    public static class ApiError {

        private HttpStatus status;
        private String message;
        private String error;

        public HttpStatus getStatus() {
            return status;
        }

        public void setStatus(HttpStatus status) {
            this.status = status;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }

        public ApiError(HttpStatus status, String message, String error) {
            super();
            this.status = status;
            this.message = message;
            this.error = error;
        }
    }
}
