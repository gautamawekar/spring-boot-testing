package com.gawekar.springboot.bank.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gawekar.springboot.bank.config.AppConfiguration;
import com.gawekar.springboot.bank.config.JacksonConfiguration;
import com.gawekar.springboot.bank.errors.GlobalExceptionHandler;
import com.gawekar.springboot.bank.service.Payment;
import com.gawekar.springboot.bank.service.PaymentService;
import org.javamoney.moneta.Money;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.money.Monetary;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * In this test case we need to explicitly set ControllerAdvice (see mockMvc creation)
 */
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AppConfiguration.class, PaymentsController.class})
public class PaymentsControllerOldStyleTest {
    @Autowired
    PaymentsController paymentsController;
    @MockBean
    private PaymentService paymentService;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Before
    public void setup() {
        //Here we need to explicitly set MessageConverter & ControllerAdvice
        //if using newer style @WebMvcTest then all we need to ensure that
        //required configuration class is added to the context & required
        //settings from the configuration is picked up.

        //We had some extra configuration inside JacksonConfiguration against the message converter.
        //we have to manually inject that settings in following fashion
        JacksonConfiguration jacksonConfiguration = new JacksonConfiguration();
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = jacksonConfiguration.requestMappingHandlerAdapter();
        Optional<HttpMessageConverter<?>> httpMessageConverter = requestMappingHandlerAdapter.getMessageConverters().stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findFirst();

        mockMvc = MockMvcBuilders.standaloneSetup(paymentsController)
                                    .setMessageConverters(httpMessageConverter.get())
                                    .setControllerAdvice(new GlobalExceptionHandler()).build();
    }

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
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("10003"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid parameter value"));

    }

    @Test
    public void createPaymentFail_ShouldExtraUnwantedParametersPassed() throws Exception {
        //GIVEN
        //PaymentDTO paymentDto = PaymentDTO.createPaymentDto("aaa", "bbb", "12.12");

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
                //.andDo(MockMvcResultHandlers.print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errorCode").value("10001"))
                .andExpect(jsonPath("$.errorMessage").value("Invalid amount"));
    }

    @Test
    public void listPayments() throws Exception {
        //GIVEN:
        List<Payment> paymentList = List.of(Payment.createPayment("ppp", "qqq", Money.of(new BigDecimal("12.33"), Monetary.getCurrency("GBP"))));
        when(paymentService.getPaymentList())
                .thenReturn(paymentList);
        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/account/payments")
                .accept(APPLICATION_JSON));
        //.andDo(print())

        //THEN:
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].from").value("ppp"))
                .andExpect(jsonPath("$[0].to").value("qqq"))
                .andExpect(jsonPath("$[0].amount").value("GBP12.33"));


    }

    @Test
    public void getPaymentStatus() throws Exception {
        //GIVEN:
        Payment payment = Payment.createPayment("ppp", "qqq", Money.of(new BigDecimal("12.33"), Monetary.getCurrency("GBP")));
        //set the status to be paid
        payment.paid();
        //remember the id
        String paymentId = payment.getId();

        when(paymentService.getPayment(anyString())).thenReturn(Optional.of(payment));


        //WHEN:
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.get("/account/payments/{paymentId}", "1234")
                .accept(APPLICATION_JSON));

        //THEN:
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("ppp"))
                .andExpect(jsonPath("$.to").value("qqq"))
                .andExpect(jsonPath("$.id").value(paymentId))
                .andExpect(jsonPath("$.paymentStatus").value("PAID"));
    }

    @Test
    public void createPayments() throws Exception {
        //GIVEN:
        PaymentDTO paymentDto = PaymentDTO.createPaymentDto("aaa", "bbb", "12.12");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders
                .post("/account/payments")
                .contentType(APPLICATION_JSON)
                .content(asJson(paymentDto)));
        //WHEN:
        ArgumentCaptor<Payment> paymentArgumentCaptor = ArgumentCaptor.forClass(Payment.class);
        resultActions.andExpect(status().isCreated());
        verify(paymentService).createPayment(paymentArgumentCaptor.capture());

        Payment value = paymentArgumentCaptor.getValue();
        assertThat(value.getFrom()).isEqualTo("aaa");
        assertThat(value.getTo()).isEqualTo("bbb");
        assertThat(value.getAmount().toString()).isEqualTo("GBP 12.12");
    }

    private String asJson(PaymentDTO paymentDTO) throws Exception {
        return objectMapper.writeValueAsString(paymentDTO);
    }

    public <T> T fromJSON(final TypeReference<T> type,
                          final String jsonPacket) {
        T data = null;
        try {
            data = objectMapper.readValue(jsonPacket, type);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return data;
    }
}
