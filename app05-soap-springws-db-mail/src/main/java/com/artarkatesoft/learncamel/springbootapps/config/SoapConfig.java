package com.artarkatesoft.learncamel.springbootapps.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapConfig {

    @Bean
    WebServiceTemplate webServiceTemplate(){
        WebServiceTemplate template = new WebServiceTemplate();
        template.setDefaultUri("{{app.soap-service.defaultUri}}");
        return template;
    }

}
