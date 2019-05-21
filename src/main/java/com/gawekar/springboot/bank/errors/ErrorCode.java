package com.gawekar.springboot.bank.errors;

public enum ErrorCode {
    NEGATIVE_OR_ZERO_AMOUNT(10001, "Invalid amount"),
    INVALID_PARAM(10002, "Invalid parameter passed"),
    INVALID_PARAM_VALUE(10003, "Invalid parameter value"),
    INVALID_PAYMENT_ID(50001, "Invalid paymentId");

    private long code;
    private String message;

    ErrorCode(long code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public long getCode() {
        return code;
    }
}
