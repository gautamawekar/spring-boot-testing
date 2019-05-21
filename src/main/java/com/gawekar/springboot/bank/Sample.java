package com.gawekar.springboot.bank;

import com.gawekar.springboot.bank.controller.PaymentDTO;
import com.gawekar.springboot.bank.service.Payment;
import org.javamoney.moneta.Money;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;

import javax.money.Monetary;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Locale;

public class Sample {
    public static void main(String[] args) {
        MonetaryAmountFormat formatUSD = MonetaryFormats.getAmountFormat(Locale.ENGLISH);
        ModelMapper modelMapper = new ModelMapper();
        Converter<String, Money> strToMoneyConvertor = new AbstractConverter<>() {
            protected Money convert(String moneyStr) {
                return Money.of(new BigDecimal(moneyStr), Monetary.getCurrency("GBP"));
            }
        };
        Converter<Money,String> moneyToStringConverter = new AbstractConverter<>(){
            @Override
            protected String convert(Money money) {
                return formatUSD.format(money);
            }
        };

        modelMapper.addConverter(strToMoneyConvertor);
        modelMapper.addConverter(moneyToStringConverter);

        PaymentDTO dto = new PaymentDTO();
        dto.setAmount("100.25");
        dto.setFrom("11111");
        dto.setTo("22222");
        dto.setId("someid");
        dto.setPaymentStatus("PAID");


        Payment payment = modelMapper.map(dto, Payment.class);

        System.out.println(payment.getAmount());
        payment.allocateId();
        payment.paid();

        PaymentDTO paymentDTO = modelMapper.map(payment, PaymentDTO.class);
        System.out.println(paymentDTO);
    }
}
