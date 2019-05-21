package com.gawekar.springboot.bank.service;


import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PaymentService {
    private final Map<String, Payment> paymentMap = new ConcurrentHashMap<>();

    public void createPayment(Payment payment) {
        payment.allocateId();
        setStatusRandomly(payment);
        paymentMap.put(payment.getId(), payment);
        System.out.println("paymentMap --> " + paymentMap);
    }

    public void delete() {

    }

    public Optional<Payment> getPayment(String paymentId) {
        return Optional.ofNullable(paymentMap.get(paymentId));
    }

    public Collection<Payment> getPaymentList() {
        return paymentMap.values();
    }

    private void setStatusRandomly(Payment payment) {
        switch (ThreadLocalRandom.current().nextInt(1, 7)) {
            case 1:
            case 3:
            case 4:
                payment.paid();
                break;
            case 2:
            case 5:
                payment.failed();
                break;
        }
    }

}
