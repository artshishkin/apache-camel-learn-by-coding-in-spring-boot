package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.processors.CountrySelectProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;

//@Component
@RequiredArgsConstructor
public class RestCamelRoute extends RouteBuilder {

//    private final BuildSQLProcessor buildSQLProcessor;
    private final MailProcessor mailProcessor;
    private final CountrySelectProcessor countrySelectProcessor;

    @Override
    public void configure() throws Exception {

//        GsonDataFormat itemFormat = new GsonDataFormat(Item.class);

//        onException(BeanValidationException.class)
//                .log(LoggingLevel.ERROR, "Error while validating bean ${body}")
//                .marshal(itemFormat)
//                .to("{{errorRoute}}")
////                .handled(true)
//        ;
//
//        onException(Exception.class)
//                .log(LoggingLevel.ERROR, "Wrong input message ${body}")
//                .process(mailProcessor)
//                .to("{{errorRoute}}")
////                .handled(true)
//        ;
//
//        onException(DataException.class)
//                .to("log:errorInRoute?level=ERROR&showProperties=true")
//                .maximumRedeliveries(3)
//                .redeliveryDelay(400)
//                .backOffMultiplier(2)
//                .maximumRedeliveryDelay(999)
//                .retryAttemptedLogLevel(LoggingLevel.ERROR)
//                .process(mailProcessor)
//                .marshal(itemFormat)
//                .to("{{errorRoute}}")
////                .handled(true)
//        ;


//        from("{{routeFromUri}}")
//                .log("Evn. is `{{message}}`")
//                .log("Read message from ActiveMQ: ${body}")
//                .unmarshal(itemFormat)
//                .log("Unmarshalled item is: ${body}")
//                .to("bean-validator://itemValidator")
//                .process(buildSQLProcessor)
//                .to("{{routeTo1Uri}}")
//                .to("{{selectNode}}")
//                .to("{{routeTo2Uri}}")
//                .log("Read items from DB are ${body}");

        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .process(countrySelectProcessor)
                .setHeader(Exchange.HTTP_METHOD, constant("GET"))
                .setHeader(Exchange.HTTP_URI, simple("https://restcountries.eu/rest/v2/alpha/${header.countryId}"))
                .to("{{routeTo1Uri}}")
                .log("Read message from REST Endpoint: ${body}")
//                .to("{{routeTo2Uri}}")
        ;


    }
}

