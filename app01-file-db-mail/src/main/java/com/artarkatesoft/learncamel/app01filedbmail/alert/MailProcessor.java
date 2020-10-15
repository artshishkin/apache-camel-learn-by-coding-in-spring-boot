package com.artarkatesoft.learncamel.app01filedbmail.alert;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MailProcessor implements Processor {

    private final JavaMailSender mailSender;

    @Value("${mailto}")
    private String mailTo;
    @Value("${mailFrom}")
    private String mailFrom;

    @Override
    public void process(Exchange exchange) throws Exception {
        Exception exception = exchange.getProperty(Exchange.EXCEPTION_CAUGHT, Exception.class);
        log.info("Exception caught in Mail processor: {}", exception.getMessage());

        String messageBody = "Exception happened in the Camel Route: " + exception.getMessage();
        SimpleMailMessage mailMessage = new SimpleMailMessage();

        mailMessage.setFrom(mailFrom);
        mailMessage.setTo(mailTo);
        mailMessage.setText(messageBody);
        mailMessage.setSubject("Exception in Camel Route");
        mailSender.send(mailMessage);
    }
}
