package com.gawekar.springboot.bank.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;
import java.util.Optional;

@Configuration
public class JacksonConfiguration extends WebMvcConfigurationSupport {
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        Optional<HttpMessageConverter<?>> httpMessageConverter = converters.stream()
                .filter(converter -> converter instanceof MappingJackson2HttpMessageConverter)
                .findFirst();
        httpMessageConverter
                .ifPresent(c -> applyProperties((MappingJackson2HttpMessageConverter) c));
    }

    private void applyProperties(MappingJackson2HttpMessageConverter c) {
        c.setPrettyPrint(true);//sets pretty printing
        c.getObjectMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, true);//no unknown data allowed
    }
}
