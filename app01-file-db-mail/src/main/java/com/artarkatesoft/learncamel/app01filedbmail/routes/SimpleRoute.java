package com.artarkatesoft.learncamel.app01filedbmail.routes;

import com.artarkatesoft.learncamel.app01filedbmail.domain.Item;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.bindy.csv.BindyCsvDataFormat;
import org.apache.camel.spi.DataFormat;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SimpleRoute extends RouteBuilder {

    private final String valueMess;
    private final Environment environment;

    public SimpleRoute(@Value("${valueMessage}") String valueMess, Environment environment) {
        this.valueMess = valueMess;
        this.environment = environment;
    }

    @Override
    public void configure() throws Exception {
        DataFormat dataFormat = new BindyCsvDataFormat(Item.class);
        from("{{startRoute}}")
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
        ;
    }
}
