package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.domain.Item;
import com.artarkatesoft.learncamel.springbootapps.exceptions.DataException;
import com.artarkatesoft.learncamel.springbootapps.processors.BuildSQLProcessor;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class SimpleRoute extends RouteBuilder {

    private final String valueMess;
    private final Environment environment;
    private final DataSource dataSource;

    @Autowired
    private BuildSQLProcessor buildSQLProcessor;

    @Autowired
    private MailProcessor mailProcessor;

    public SimpleRoute(@Value("${valueMessage}") String valueMess, Environment environment, @Qualifier("dataSource") DataSource dataSource) {
        this.valueMess = valueMess;
        this.environment = environment;
        this.dataSource = dataSource;
    }

    @Override
    public void configure() throws Exception {
        DataFormat dataFormat = new BindyCsvDataFormat(Item.class);

//        errorHandler(deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true"));

//        errorHandler(deadLetterChannel("log:errorInRoute?level=ERROR&showProperties=true")
//                .maximumRedeliveries(5)
//                .redeliveryDelay(300)
//                .backOffMultiplier(2)
//                .maximumRedeliveryDelay(1200)
//                .retryAttemptedLogLevel(LoggingLevel.ERROR)
//        );

        onException(BeanValidationException.class)
                .log(LoggingLevel.ERROR, "Error while validating bean ${body}")
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


        from("{{startRoute}}").routeId("mainRoute")
                .log("Timer invoked and evn. is `{{message}}` and Headers are ${headers}")

                .choice()
                .when(header("env").isNotEqualTo("mock"))
                .pollEnrich("{{routeFromUri}}")
                .log("We have " + valueMess + " in " + environment.getProperty("envMessage") + " and copy File with content:\n${body}")
                .otherwise()
                .log("Mock env flow and the body is ${body} ")
                .end()

                .filter(body().isNotNull())
                .to("{{routeTo1Uri}}")
                .unmarshal(dataFormat)
                .log("Unmarshalled items are `${body}`")
                .split(body())
                .log("Split item is ${body}")
                .to("bean-validator://artBeanValidator")
                .process(buildSQLProcessor)
                .to("{{routeTo2Uri}}")
                .end()
                .setBody(constant("Data Updated Successfully"))
                .to("{{routeTo3Uri}}")
        ;
    }
}
