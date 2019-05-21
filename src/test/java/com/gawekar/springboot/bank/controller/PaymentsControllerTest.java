package com.gawekar.springboot.bank.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gawekar.springboot.bank.config.AppConfiguration;
import com.gawekar.springboot.bank.config.JacksonConfiguration;
import com.gawekar.springboot.bank.service.Payment;
import com.gawekar.springboot.bank.service.PaymentService;
import org.javamoney.moneta.Money;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.verify;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * In this test case we do not explicitly need to setup ControllerAdvice.
 */

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = {PaymentsController.class})
@Import({AppConfiguration.class, JacksonConfiguration.class})
public class PaymentsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PaymentService paymentService;

    private ObjectMapper objectMapper = new ObjectMapper();


    @Test
    public void paymentCreationFail_ShouldFromIsMissing() throws Exception {
        //GIVEN
        PaymentDTO paymentDto = PaymentDTO.createPaymentDto(null, "bbb", "12.12");

        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/account/payments")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(asJson(paymentDto)));

        //THEN:
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("10003"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid parameter value"));
        //System.out.println(mvcResult.getResponse().getContentAsString());


    }

    @Test
    public void paymentCreationFail_ShouldAmountIsNegative() throws Exception {
        //GIVEN
        PaymentDTO paymentDto = PaymentDTO.createPaymentDto("aaa", "bbb", "-12.12");

        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/account/payments")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(asJson(paymentDto)));

        //THEN:
        resultActions
                .andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("10001"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid amount"));
//        MvcResult mvcResult = resultActions.andReturn();
//        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    public void createPayment() throws Exception {
        //GIVEN
        PaymentDTO paymentDto = PaymentDTO.createPaymentDto("aaa", "bbb", "12.12");

        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/account/payments")
                .contentType(APPLICATION_JSON)
                .content(asJson(paymentDto)));

        //THEN:
        resultActions
                .andExpect(status().isCreated());
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        resultActions.andExpect(status().isCreated());
        verify(paymentService).createPayment(paymentArgumentCaptor.capture());

        Payment value = paymentArgumentCaptor.getValue();
        assertThat(value.getFrom()).isEqualTo("aaa");
        assertThat(value.getTo()).isEqualTo("bbb");
        assertThat(value.getAmount().toString()).isEqualTo("GBP 12.12");
    }

    @Test
    public void listPayments() throws Exception {
        //GIVEN:
        List<Payment> paymentList = List.of(Payment.createPayment("ppp", "qqq", Money.of(new BigDecimal("12.33"), Monetary.getCurrency("GBP"))));
        Mockito.when(paymentService.getPaymentList())
                .thenReturn(paymentList);
        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/account/payments")
                .accept(APPLICATION_JSON));

        //THEN:
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].from").value("ppp"))
                .andExpect(jsonPath("$[0].to").value("qqq"))
                .andExpect(jsonPath("$[0].amount").value("GBP12.33"));

    }

    @Test
    public void createPaymentFail_ShouldExtraUnwantedParametersPassed() throws Exception {
        //GIVEN
        //This test specially to test configuration defined inside JacksonConfiguration

        class PaymentDTOExtended extends PaymentDTO {
            private String extraParam;

            public void setExtraParam(String extraParam) {
                this.extraParam = extraParam;
            }

            public String getExtraParam() {
                return extraParam;
            }
        }

        PaymentDTOExtended paymentDto = new PaymentDTOExtended();
        paymentDto.setExtraParam("extraUnwantedValue");
        paymentDto.setFrom("ppp");
        paymentDto.setTo("qqq");
        paymentDto.setAmount("12.12");

        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/account/payments")
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .content(asJson(paymentDto)));

        //THEN:
        resultActions
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("10002"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid parameter passed"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("UnrecognizedPropertyException")));
    }


    String asJson(PaymentDTO paymentDTO) throws Exception {
        return objectMapper.writeValueAsString(paymentDTO);
    }


}