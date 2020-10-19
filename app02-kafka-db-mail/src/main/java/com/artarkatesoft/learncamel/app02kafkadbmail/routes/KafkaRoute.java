package com.artarkatesoft.learncamel.app02kafkadbmail.routes;

import com.artarkatesoft.learncamel.app00commons.domain.Item;
import com.artarkatesoft.learncamel.app00commons.exceptions.DataException;
import com.artarkatesoft.learncamel.app02kafkadbmail.alert.MailProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.gson.GsonDataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class KafkaRoute extends RouteBuilder {

    private final Environment environment;

    @Autowired
    private MailProcessor mailProcessor;

    public KafkaRoute(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void configure() throws Exception {

        GsonDataFormat itemFormat = new GsonDataFormat(Item.class);

        onException(BeanValidationException.class)
                .log(LoggingLevel.ERROR, "Error while validating bean ${body}")
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
//                .handled(true)
        ;


        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .log("Read message from Kafka: ${body}")
                .unmarshal(itemFormat)
                .log("Unmarshall message is ${body}")
                .to("bean-validator://itemValidator")
                .to("{{routeTo1Uri}}")


        ;
    }
}

