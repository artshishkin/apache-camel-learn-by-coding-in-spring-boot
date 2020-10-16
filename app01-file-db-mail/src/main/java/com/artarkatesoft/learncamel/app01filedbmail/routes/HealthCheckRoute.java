package com.artarkatesoft.learncamel.app01filedbmail.routes;

import com.artarkatesoft.learncamel.app01filedbmail.processors.HealthCheckProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthCheckRoute extends RouteBuilder {

    private final  HealthCheckProcessor healthCheckProcessor;

    @Override
    public void configure() throws Exception {
        from("{{healthRouteStart}}").routeId("healthRoute")
                .pollEnrich("{{healthRouteUri}}")
                .process(healthCheckProcessor)
                .filter(exchange -> exchange.getProperty(Exchange.EXCEPTION_CAUGHT).)
    }
}
