package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.processors.HealthCheckProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class HealthCheckRoute extends RouteBuilder {

    private final HealthCheckProcessor healthCheckProcessor;
    private final MailProcessor mailProcessor;

    @Override
    public void configure() throws Exception {
        Predicate isNotMock = header("env").isNotEqualTo("mock");
        from("{{healthRouteStart}}").routeId("healthRoute")
                .choice()
                .when(isNotMock).pollEnrich("{{healthRouteUri}}").end()
                .process(healthCheckProcessor)
                .filter(header("error"))
                .process(mailProcessor);
    }
}
