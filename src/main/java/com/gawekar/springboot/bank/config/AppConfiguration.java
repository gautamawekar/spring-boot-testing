package com.gawekar.springboot.bank.config;

import org.javamoney.moneta.Money;
import org.modelmapper.AbstractConverter;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.money.Monetary;
import javax.money.format.MonetaryAmountFormat;
import javax.money.format.MonetaryFormats;
import java.math.BigDecimal;
import java.util.Locale;

@Configuration
public class AppConfiguration {
    @Bean
    public ModelMapper modelMapper() {
        MonetaryAmountFormat format = MonetaryFormats.getAmountFormat(Locale.ENGLISH);
        ModelMapper modelMapper = new ModelMapper();
        Converter<String, Money> strToMoneyConvertor = new AbstractConverter<>() {
            protected Money convert(String moneyStr) {
                return Money.of(new BigDecimal(moneyStr), Monetary.getCurrency("GBP"));
            }
        };
        Converter<Money,String> moneyToStringConverter = new AbstractConverter<>(){
            @Override
            protected String convert(Money money) {
                return format.format(money);
            }
        };

        modelMapper.addConverter(strToMoneyConvertor);
        modelMapper.addConverter(moneyToStringConverter);
        return modelMapper;
    }
}
