package com.artarkatesoft.learncamel.springbootapps.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class CountrySelectProcessor implements Processor {

    private final List<String> countries = List.of("ua", "us", "jp", "gb", "in", "it");
    private final Random random = new Random();

    @Override
    public void process(Exchange exchange) throws Exception {
        int rand = random.nextInt(countries.size());
        String countryCode = countries.get(rand);
        log.info("Selected country code is {}", countryCode);
        exchange.getIn().setHeader("countryId", countryCode);
    }
}
