package com.gawekar.springboot.bank.errors;

import com.gawekar.springboot.bank.errors.exception.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 400

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        log.error("Invalid request: {}", ExceptionUtils.getRootCauseMessage(ex));
        return handleExceptionInternal(ex, createMessage(ErrorCode.INVALID_PARAM, ex), headers, HttpStatus.BAD_REQUEST, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        //This error is called because of @Valid annotation set against RequestEntity
        log.error("Method argument not valid", ex);
        return handleExceptionInternal(ex, createMessage(ErrorCode.INVALID_PARAM_VALUE, ex), headers, HttpStatus.BAD_REQUEST, request);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Object> handleValidationException(ValidationException ex, WebRequest request) {
        log.error("Validation failure", ex);
        return handleExceptionInternal(ex, createMessage(ex.getErrorCode(), ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
    }

//    @ExceptionHandler(UnrecognizedPropertyException.class)
//    public ResponseEntity<Object> handleUnrecognizedPropertyException(UnrecognizedPropertyException ex, WebRequest request) {
//        log.error("Validation failure", ex);
//        return handleExceptionInternal(ex, createMessage(ErrorCode.INVALID_PARAM_VALUE, ex), new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
//    }

    private ValidationErrorResponse createMessage(ErrorCode errorCode, Exception ex) {
        return new ValidationErrorResponse(errorCode.getCode(), errorCode.getMessage(), ExceptionUtils.getRootCauseMessage(ex));
    }
}
