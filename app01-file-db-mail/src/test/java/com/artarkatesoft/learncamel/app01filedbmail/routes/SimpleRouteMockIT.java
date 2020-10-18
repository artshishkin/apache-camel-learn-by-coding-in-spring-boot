package com.artarkatesoft.learncamel.app01filedbmail.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.ExcludeRoutes;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("{{routeFromUri}}")
@ExcludeRoutes({HealthCheckRoute.class})
class SimpleRouteMockIT {

    @Autowired
    ProducerTemplate template;

    @EndpointInject("{{routeTo1Uri}}")
    private MockEndpoint mockEndpoint;

    @EndpointInject("{{routeTo2Uri}}")
    private MockEndpoint mockEndpoint2;

    @EndpointInject("{{routeTo3Uri}}")
    private MockEndpoint mockEndpoint3;

    @Value("${spring.profiles.active}")
    String profile;

    @Autowired
    Environment environment;

    @Test
    void testMoveFileMock() throws InterruptedException {
        //given
        String fileContent = "type,sku#,item_description,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,300";

        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(fileContent);

        //when
//        template.sendBodyAndHeader("{{startRoute}}", fileContent, "env", profile);
        template.sendBodyAndHeader("{{startRoute}}", fileContent, "env", environment.getActiveProfiles()[0]);

        //then
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    void testMoveFileMock_ADD() throws InterruptedException {
        //given
        String fileContent = "type,sku#,item_description,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,300";

        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(fileContent);

        mockEndpoint2.expectedMessageCount(2);
        mockEndpoint2.expectedBodiesReceived(
                "INSERT INTO items (sku, items_description, price) VALUES (:?sku,:?itemDescription,:?itemPrice);",
                "INSERT INTO items (sku, items_description, price) VALUES (:?sku,:?itemDescription,:?itemPrice);"
        );
        mockEndpoint2.expectedHeaderValuesReceivedInAnyOrder("sku", List.of("100", "101"));
        mockEndpoint2.expectedHeaderValuesReceivedInAnyOrder("itemDescription", List.of("Samsung TV", "LG TV"));
        mockEndpoint2.expectedHeaderValuesReceivedInAnyOrder("itemPrice", List.of(300.00, 500.00));

        mockEndpoint3.expectedMessageCount(1);
        mockEndpoint3.expectedBodiesReceived("Data Updated Successfully");

        //when
        template.sendBodyAndHeader("{{startRoute}}", fileContent, "env", environment.getActiveProfiles()[0]);

        //then
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    void testMoveFileMock_UPDATE() throws InterruptedException {
        //given
        String fileContent = "type,sku#,item_description,price\n" +
                "UPDATE,100,Samsung TV1,450";

        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(fileContent);

        mockEndpoint2.expectedMessageCount(1);
        mockEndpoint2.expectedBodiesReceived(
                "UPDATE items SET (items_description, price) = ( :?itemDescription, :?itemPrice ) WHERE sku = :?sku;"
        );

        mockEndpoint2.expectedHeaderReceived("sku", "100");
        mockEndpoint2.expectedHeaderReceived("itemDescription", "Samsung TV1");
        mockEndpoint2.expectedHeaderReceived("itemPrice", 450.00);

        mockEndpoint3.expectedMessageCount(1);
        mockEndpoint3.expectedBodiesReceived("Data Updated Successfully");

        //when
        template.sendBodyAndHeader("{{startRoute}}", fileContent, "env", environment.getActiveProfiles()[0]);

        //then
        mockEndpoint.assertIsSatisfied();
    }

    @Test
    void testMoveFileMock_DELETE() throws InterruptedException {
        //given
        String fileContent = "type,sku#,item_description,price\n" +
                "DELETE,100,Samsung TV1,450";

        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(fileContent);

        mockEndpoint2.expectedMessageCount(1);
        mockEndpoint2.expectedBodiesReceived(
                "DELETE FROM items WHERE sku = :?sku;"
        );
        mockEndpoint2.expectedHeaderReceived("sku", "100");

        mockEndpoint3.expectedMessageCount(1);
        mockEndpoint3.expectedBodiesReceived("Data Updated Successfully");

        //when
        template.sendBodyAndHeader("{{startRoute}}", fileContent, "env", environment.getActiveProfiles()[0]);

        //then
        mockEndpoint.assertIsSatisfied();
    }
}