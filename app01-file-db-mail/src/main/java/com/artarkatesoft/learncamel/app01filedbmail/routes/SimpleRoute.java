package com.artarkatesoft.learncamel.app01filedbmail.routes;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class SimpleRoute extends RouteBuilder {

    @Value("${valueMessage}")
    String valueMess;

    @Autowired
    Environment environment;

    @Override
    public void configure() throws Exception {
        from("{{routeTimer}}")
                .log("Timer invoked and evn. is `{{message}}`")
                .pollEnrich("{{routeFromUri}}")
                .log("We have " + valueMess + " in " + environment.getProperty("envMessage") + " and copy File with content:\n${body}")
                .filter(body().isNotNull())
                .to("{{routeTo1Uri}}");
    }
}
