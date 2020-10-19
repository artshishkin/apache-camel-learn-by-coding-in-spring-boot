package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.domain.Item;
import com.artarkatesoft.learncamel.springbootapps.exceptions.DataException;
import com.artarkatesoft.learncamel.springbootapps.processors.BuildSQLProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.gson.GsonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class KafkaRoute extends RouteBuilder {

    private final Environment environment;
    private final BuildSQLProcessor buildSQLProcessor;

    @Autowired
    private MailProcessor mailProcessor;


    @Override
    public void configure() throws Exception {

        GsonDataFormat itemFormat = new GsonDataFormat(Item.class);

        onException(BeanValidationException.class)
                .log(LoggingLevel.ERROR, "Error while validating bean ${body}")
                .to("{{errorRoute}}")
//                .handled(true)
        ;

        onException(Exception.class)
                .log(LoggingLevel.ERROR, "Wrong input message ${body}")
                .to("{{errorRoute}}")
                .process(mailProcessor)
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
                .to("{{errorRoute}}")
//                .handled(true)
        ;


        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .log("Read message from Kafka: ${body}")
                .unmarshal(itemFormat)
                .log("Unmarshall message is ${body}")
                .to("bean-validator://itemValidator")
                .process(buildSQLProcessor)
                .to("{{routeTo1Uri}}")
                .to("{{selectNode}}")
                .log("Read items from DB are ${body}")
        ;
    }
}

