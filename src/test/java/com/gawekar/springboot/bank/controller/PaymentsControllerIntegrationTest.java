package com.gawekar.springboot.bank.controller;

import com.gawekar.springboot.bank.errors.ErrorCode;
import com.gawekar.springboot.bank.errors.ValidationErrorResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static com.gawekar.springboot.bank.controller.PaymentDTO.createPaymentDto;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

//import org.junit.jupiter.api.Test;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PaymentsControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @LocalServerPort
    int port;

    @Test
    public void createPayment() {
        //GIVEN:
        PaymentDTO paymentDTO = createPaymentDto("aaa", "bbb", "10.20");
        //WHEN:
        ResponseEntity<Void> voidResponseEntity = restTemplate.postForEntity("/account/payments", paymentDTO, Void.class);
        //THEN
        assertThat(voidResponseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    public void getPayments_ShouldReturnPaymentList_WhenCreatedPaymentIsSuccessful() {
        //GIVEN:
        PaymentDTO paymentDTO = createPaymentDto("ppp", "qqq", "11.20");
        restTemplate.postForEntity("/account/payments", paymentDTO, Void.class);

        //WHEN:
        ResponseEntity<List<PaymentDTO>> paymentDtoResponseEntity = restTemplate.exchange("http://localhost:" + port + "/account/payments", HttpMethod.GET, null, new ParameterizedTypeReference<List<PaymentDTO>>() {
        });

        //THEN:
        List<PaymentDTO> paymentDtoList = paymentDtoResponseEntity.getBody();
        PaymentDTO onlyPaymentDto = paymentDtoList.get(0);
        assertThat(onlyPaymentDto.getFrom()).isEqualTo("ppp");
        assertThat(onlyPaymentDto.getTo()).isEqualTo("qqq");
        assertThat(onlyPaymentDto.getAmount()).isEqualTo("GBP11.20");
        assertThat(onlyPaymentDto.getId()).isNotEmpty();
        assertThat(onlyPaymentDto.getPaymentStatus()).isNotEmpty();

    }

    @Test
    public void createPayment_ShouldReturnError400_WhenFromIsNotSet() {
        //GIVEN:
        PaymentDTO paymentDTO = createPaymentDto(null, "bbb", "10.20");

        //WHEN:
        ResponseEntity<ValidationErrorResponse> validationErrorResponse = restTemplate.postForEntity("/account/payments", paymentDTO, ValidationErrorResponse.class);

        //THEN:
        assertThat(validationErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorResponse.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM_VALUE.getCode());
        assertThat(validationErrorResponse.getBody().getErrorMessage()).isEqualTo("Invalid parameter value");
        assertThat(validationErrorResponse.getBody().getDetailedErrorMessage()).contains("Field error in object 'paymentDTO' on field 'from'");
    }

    @Test
    public void createPayment_ShouldReturnError400_WhenToIsNotSet() {
        //GIVEN:
        PaymentDTO paymentDTO = createPaymentDto("aaa", null, "10.20");

        //WHEN:
        ResponseEntity<ValidationErrorResponse> validationErrorResponse = restTemplate.postForEntity("/account/payments", paymentDTO, ValidationErrorResponse.class);

        //THEN:
        assertThat(validationErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorResponse.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM_VALUE.getCode());
        assertThat(validationErrorResponse.getBody().getErrorMessage()).isEqualTo("Invalid parameter value");
        assertThat(validationErrorResponse.getBody().getDetailedErrorMessage()).contains("Field error in object 'paymentDTO' on field 'to'");
    }

    @Test
    public void createPayment_ShouldReturnError400_WhenAmountIsNotSet() {
        //GIVEN:
        PaymentDTO paymentDTO = createPaymentDto("aaa", "bbb", null);

        //WHEN:
        ResponseEntity<ValidationErrorResponse> validationErrorResponse = restTemplate.postForEntity("/account/payments", paymentDTO, ValidationErrorResponse.class);

        //THEN:
        assertThat(validationErrorResponse.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(validationErrorResponse.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_PARAM_VALUE.getCode());
        assertThat(validationErrorResponse.getBody().getErrorMessage()).isEqualTo("Invalid parameter value");
        assertThat(validationErrorResponse.getBody().getDetailedErrorMessage()).contains("Field error in object 'paymentDTO' on field 'amount'");
    }


}