package com.artarkatesoft.learncamel.app01filedbmail.routes;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

class SimpleRouteTest extends CamelTestSupport {

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new SimpleRoute();
    }
}
