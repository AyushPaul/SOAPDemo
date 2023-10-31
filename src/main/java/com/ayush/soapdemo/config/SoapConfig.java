package com.ayush.soapdemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.soap.saaj.SaajSoapMessageFactory;

@Configuration
public class SoapConfig {
    @Bean
    public SaajSoapMessageFactory saajSoapMessageFactory() {
        return new SaajSoapMessageFactory();
    }
}
