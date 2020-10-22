package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import com.artarkatesoft.learncamel.springbootapps.exceptions.DataException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.jackson.JacksonDataFormat;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class CountryRestServerRoute extends RouteBuilder {

    private final MailProcessor mailProcessor;

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


        restConfiguration()
                .component("servlet")
                // Allow Camel to try to marshal/unmarshal between Java objects and JSON
                .bindingMode(RestBindingMode.auto);


//        rest("/countries").get("/").to("log:apiLog?showAll=true");


//        from("rest:get:api/{countryCode}")
//                .log("get request. body is `${body}`, Headers are: `${headers}`")
//                .transform().simple("CODE IS ${headers.countryCode}");

        // Now define the REST API (POST, GET, etc.)
        rest()
                .path("/api") // This makes the API available at http://host:port/$CONTEXT_ROOT/api

                .consumes("application/json")
                .produces("application/json")

                // HTTP: GET /api
                .get("{countryCode}")
                    .route()
                        .to("log:meLog?showAll=true")
                        .transform(simple("Country Code is ${headers.countryCode}"))
                    .endRest()
                .get("/country/{countryCode}")
                    .route()
                        .to("log:meLog?showAll=true")
                        .transform(simple("Code is ${headers.countryCode}"))
                    .endRest()

                .post("/countries")
                    .type(Country.class)
                    .route()
                    .to("log:meLog?showAll=true")
                    .to("bean-validator://countryValidator")
                    .setHeader("countryId",simple("${body.countryCode}"))
                    .setBody(simple("INSERT INTO country (name, country_code, population) VALUES ('${body.name}','${body.countryCode}',${body.population});"))
                    .log("Final query is ${body}")
                    .to("{{dbRoute}}")
                    .log("After DB insert body: `${body}` and Headers: ${headers}")
                    .to("log:dbLog?showAll=true")
                    .to("{{selectNode}}")
                    .to("log:logSelect?showAll=true")

                .endRest()
        ;



    }
}
