package com.gawekar.springboot.bank.controller;

import com.gawekar.springboot.bank.errors.exception.ValidationException;
import com.gawekar.springboot.bank.service.Payment;
import com.gawekar.springboot.bank.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.gawekar.springboot.bank.errors.ErrorCode.INVALID_PAYMENT_ID;

@RestController
@RequestMapping(value = "account/payments")
@Slf4j
public class PaymentsController {

    @Autowired
    private PaymentService paymentService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping()
    public ResponseEntity<Void> setupPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        PaymentCheck.assertPositiveAmount(paymentDTO);
        Payment payment = convert(paymentDTO);
        paymentService.createPayment(payment);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @GetMapping("/{paymentId}")
    @ResponseBody
    public PaymentDTO getPaymentStatus(@PathVariable String paymentId) {
        PaymentDTO paymentDTO = paymentService.getPayment(paymentId)
                .map(payment -> convert(payment))
                .orElseThrow(() -> new ValidationException(INVALID_PAYMENT_ID, "paymentId not found"));
        return paymentDTO;
    }

    @GetMapping
    public List<PaymentDTO> getPaymemts() {
        List<PaymentDTO> collect = paymentService.getPaymentList()
                .stream().map(p -> convert(p))
                .collect(Collectors.toList());
        return collect;
    }

    private Payment convert(PaymentDTO paymentDto) {
        return this.modelMapper.map(paymentDto, Payment.class);
    }

    private PaymentDTO convert(Payment payment) {
        return this.modelMapper.map(payment, PaymentDTO.class);
    }
}
