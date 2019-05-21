package com.gawekar.springboot.bank.errors;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ValidationErrorResponse {
    private long errorCode;
    private String errorMessage;
    private String detailedErrorMessage;

}
