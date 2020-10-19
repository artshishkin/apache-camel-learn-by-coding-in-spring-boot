package com.artarkatesoft.learncamel.springbootapps.routes;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit5.CamelTestSupport;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;

import javax.sql.DataSource;

@Disabled("TOO many to replace")
@ExtendWith(MockitoExtension.class)
class SimpleRouteMockUnitTest extends CamelTestSupport {

    @Mock
    Environment environment;

    @Mock
    DataSource dataSource;

    @Override
    protected RoutesBuilder createRouteBuilder() throws Exception {
        return new SimpleRoute("Value Message", environment, dataSource);
    }

    @Test
    void testMoveFileMock() throws InterruptedException {
        //given
        String fileContent = "type,sku#,item_description,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,300";
        MockEndpoint mockEndpoint = getMockEndpoint("mock:output");
        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(fileContent);

        //when
        template.sendBodyAndHeader("direct:input", fileContent, "env", "mock");

        //then
        mockEndpoint.assertIsSatisfied();
    }

}