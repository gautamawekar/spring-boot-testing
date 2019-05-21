package com.gawekar.springboot.bank.controller;

import com.gawekar.springboot.bank.errors.ErrorCode;
import com.gawekar.springboot.bank.errors.ValidationErrorResponse;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PaymentsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void createPayment() {
        //GIVEN:
        PaymentDTO paymentDTO = PaymentDTO.createPaymentDto("aaa", "bbb", "10.20");
        //WHEN:
        ResponseEntity<Void> voidResponseEntity = restTemplate.postForEntity("/account/payments", paymentDTO, Void.class);
        //THEN
        assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

//    public void getPaymemts_ShouldReturnCreatedPayment(){
//        //GIVEN:
//        PaymentDTO paymentDTO = createPaymentDto("ppp", "qqq", "11.20");
//        restTemplate.postForEntity("/account/payments", paymentDTO, Void.class);
//        //WHEN:
//        restTemplate.getForEntity("/account/payments", List<PaymentDTO>.class)
//    }

    @Test
    public void createPayment_ShouldReturnError400_WhenFromIsNotSet() {
        PaymentDTO paymentDTO = PaymentDTO.createPaymentDto(null, "bbb", "10.20");
        ResponseEntity<ValidationErrorResponse> validationErrorResponse = restTemplate.postForEntity("/account/payments", paymentDTO, ValidationErrorResponse.class);
        assertThat(validationErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorResponse.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM_VALUE.getCode());
        assertThat(validationErrorResponse.getBody().getErrorMessage()).isEqualTo("Invalid parameter value");
        assertThat(validationErrorResponse.getBody().getDetailedErrorMessage()).contains("Field error in object 'paymentDTO' on field 'from'");
    }

    @Test
    public void createPayment_ShouldReturnError400_WhenToIsNotSet() {
        PaymentDTO paymentDTO = PaymentDTO.createPaymentDto("aaa", null, "10.20");
        ResponseEntity<ValidationErrorResponse> validationErrorResponse = restTemplate.postForEntity("/account/payments", paymentDTO, ValidationErrorResponse.class);
        assertThat(validationErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorResponse.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM_VALUE.getCode());
        assertThat(validationErrorResponse.getBody().getErrorMessage()).isEqualTo("Invalid parameter value");
        assertThat(validationErrorResponse.getBody().getDetailedErrorMessage()).contains("Field error in object 'paymentDTO' on field 'to'");
    }

    @Test
    public void createPayment_ShouldReturnError400_WhenAmountIsNotSet() {
        PaymentDTO paymentDTO = PaymentDTO.createPaymentDto("aaa", "bbb", null);
        ResponseEntity<ValidationErrorResponse> validationErrorResponse = restTemplate.postForEntity("/account/payments", paymentDTO, ValidationErrorResponse.class);
        assertThat(validationErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorResponse.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM_VALUE.getCode());
        assertThat(validationErrorResponse.getBody().getErrorMessage()).isEqualTo("Invalid parameter value");
        assertThat(validationErrorResponse.getBody().getDetailedErrorMessage()).contains("Field error in object 'paymentDTO' on field 'amount'");
    }


}