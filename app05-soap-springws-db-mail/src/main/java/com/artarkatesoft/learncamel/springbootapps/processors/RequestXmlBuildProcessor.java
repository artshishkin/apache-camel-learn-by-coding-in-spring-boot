package com.artarkatesoft.learncamel.springbootapps.processors;

import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class RequestXmlBuildProcessor implements Processor {

    public static final List<String> COUNTRIES = List.of("UA", "US", "JP", "GB", "IN", "IT");
    private final Random random = new Random();
    private final String soapMessageSendFormat = "<FullCountryInfo xmlns=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
            "      <sCountryISOCode>%s</sCountryISOCode>\n" +
            "    </FullCountryInfo>";

    @Override
    public void process(Exchange exchange) throws Exception {
        int rand = random.nextInt(COUNTRIES.size());
        String countryCode = COUNTRIES.get(rand).toUpperCase();
        log.info("Selected country code is {}", countryCode);
        String soapMessageSend = String.format(soapMessageSendFormat, countryCode);
        exchange.getIn().setBody(soapMessageSend);
    }
}
