package com.gawekar.springboot.bank.controller;

import com.fasterxml.jackson.annotation.JsonRootName;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
@JsonRootName("payment")
public class PaymentDTO {
    private String id;
    @NotNull
    private String from;
    @NotNull
    private String to;
    @NotNull
    private String amount;
    private String paymentStatus;

    public static PaymentDTO createPaymentDto(String from, String to, String amount) {
        PaymentDTO paymentDTO = new PaymentDTO();
        paymentDTO.setFrom(from);
        paymentDTO.setTo(to);
        paymentDTO.setAmount(amount);
        return paymentDTO;
    }
}
