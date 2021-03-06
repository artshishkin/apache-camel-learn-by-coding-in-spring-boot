package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import com.artarkatesoft.learncamel.springbootapps.exceptions.DataException;
import com.artarkatesoft.learncamel.springbootapps.processors.CountrySelectProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CountryRestClientCamelRoute extends RouteBuilder {

    private final MailProcessor mailProcessor;
    private final CountrySelectProcessor countrySelectProcessor;

    @Override
    public void configure() throws Exception {

        JacksonDataFormat countryFormat = new JacksonDataFormat(Country.class);

        onException(BeanValidationException.class)
                .log(LoggingLevel.ERROR, "Error while validating bean ${body}")
                .marshal(countryFormat)
                .to("{{errorRoute}}")
//                .handled(true)
        ;

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Wrong input message ${body}")
                .process(mailProcessor)
                .to("{{errorRoute}}")
//                .handled(true)
        ;

        onException(DataException.class)
                .to("log:errorInRoute?level=ERROR&showProperties=true")
                .maximumRedeliveries(3)
                .redeliveryDelay(400)
                .backOffMultiplier(2)
                .maximumRedeliveryDelay(999)
                .retryAttemptedLogLevel(LoggingLevel.ERROR)
                .process(mailProcessor)
        ;

        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .process(countrySelectProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_URI, simple("https://restcountries.eu/rest/v2/alpha/${header.countryId}"))
                .to("{{routeTo1Uri}}")
                .convertBodyTo(String.class)
                .log("Read message from REST Endpoint: ${body}")
                .setHeader(Exchange.HTTP_METHOD, constant("POST"))
                .removeHeader(Exchange.HTTP_URI)
                .setHeader(Exchange.CONTENT_TYPE, constant(MediaType.APPLICATION_JSON_VALUE))
                .to("{{routeTo2Uri}}")
                .log("saved Country `${body}`")
        ;


    }
}

