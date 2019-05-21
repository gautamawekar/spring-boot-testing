package com.gawekar.springboot.bank.controller;

import com.gawekar.springboot.bank.errors.ErrorCode;
import com.gawekar.springboot.bank.errors.exception.ValidationException;

import java.math.BigDecimal;

public class PaymentCheck {
    public static void assertPositiveAmount(PaymentDTO payment) {

        if (new BigDecimal(payment.getAmount()).doubleValue() <= 0) {
            throw new ValidationException(ErrorCode.NEGATIVE_OR_ZERO_AMOUNT, "Incorrect payment amount. Value less than or equal to zero");
        }
    }
}
