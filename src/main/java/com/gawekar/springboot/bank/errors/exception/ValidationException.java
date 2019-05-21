package com.gawekar.springboot.bank.errors.exception;

import com.gawekar.springboot.bank.errors.ErrorCode;

public class ValidationException extends RuntimeException{

    private final ErrorCode errorCode;

    public ValidationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
