package com.artarkatesoft.learncamel.springbootapps.routes;

import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SoapCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .to("{{routeTo1Uri}}")
                .to("{{routeTo3Uri}}")
                ;
    }
}
