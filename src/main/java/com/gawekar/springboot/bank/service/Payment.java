package com.gawekar.springboot.bank.service;

import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.javamoney.moneta.Money;

import java.util.concurrent.ThreadLocalRandom;

@Data
public class Payment {
    @Setter(AccessLevel.NONE)//do not let anyone set ID externally
    private String id;
    private String from;
    private String to;
    private Money amount;
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Status paymentStatus = Status.PENDING;//do not let dto conversion change the status

    enum Status {
        PENDING, PAID, FAILED;
    }

    public void paid() {
        this.paymentStatus = Status.PAID;
    }

    public void failed() {
        this.paymentStatus = Status.FAILED;
    }

    public String getPaymentStatus() {
        return this.paymentStatus.toString();
    }

    public void allocateId() {
        if (id == null) {
            this.id = String.format("%05d", ThreadLocalRandom.current().nextInt(1, 5000));
        } else {
            throw new RuntimeException("Invalid call - cannot assign new Id to existing payment");
        }

    }

    public static Payment createPayment(String from, String to, Money amount) {
        Payment payment = new Payment();
        payment.setAmount(amount);
        payment.setFrom(from);
        payment.setTo(to);
        payment.allocateId();
        return payment;
    }
}
