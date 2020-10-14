package com.artarkatesoft.learncamel.app01filedbmail.routes;

import org.apache.camel.EndpointInject;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SimpleRouteMockIT {

    @Autowired
    ProducerTemplate template;

    @EndpointInject("{{routeTo1Uri}}")
    private MockEndpoint mockEndpoint;

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

}