package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActiveMQRoute extends RouteBuilder {

//    private final BuildSQLProcessor buildSQLProcessor;

    private final MailProcessor mailProcessor;

    @Override
    public void configure() throws Exception {
//
//        GsonDataFormat itemFormat = new GsonDataFormat(Item.class);
//
//        onException(BeanValidationException.class)
//                .log(LoggingLevel.ERROR, "Error while validating bean ${body}")
//                .to("{{errorRoute}}")
////                .handled(true)
//        ;
//
//        onException(Exception.class)
//                .log(LoggingLevel.ERROR, "Wrong input message ${body}")
//                .to("{{errorRoute}}")
//                .process(mailProcessor)
////                .handled(true)
//        ;

//        onException(DataException.class)
//                .to("log:errorInRoute?level=ERROR&showProperties=true")
//                .maximumRedeliveries(3)
//                .redeliveryDelay(400)
//                .backOffMultiplier(2)
//                .maximumRedeliveryDelay(999)
//                .retryAttemptedLogLevel(LoggingLevel.ERROR)
//                .process(mailProcessor)
//                .to("{{errorRoute}}")
////                .handled(true)
//        ;


        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .log("Read message from ActiveMQ: ${body}")
                .to("{{routeTo1Uri}}");
    }
}

