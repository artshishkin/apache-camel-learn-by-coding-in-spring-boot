package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.then;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("mock")
@TestPropertySource(properties = {"spring.mail.username=foo", "spring.mail.password=bar"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpointsAndSkip("{{startRoute}}")
//@ExcludeRoutes(SimpleRoute.class)
class HealthCheckRouteTest {

    @Autowired
    ProducerTemplate template;

    @MockBean
    MailProcessor mailProcessor;

    @Test
    void testHealthRoute() {
        //given
        String statusMessage = "{\"status\":\"UP\",\"components\":{\"camelHealth\":{\"status\":\"UP\",\"details\":{\"name\":\"camel-health-check\",\"context\":\"UP\",\"route:healthRoute\":\"UP\",\"route:mainRoute\":\"UP\"}},\"db\":{\"status\":\"UP\",\"details\":{\"database\":\"PostgreSQL\",\"validationQuery\":\"isValid()\"}},\"diskSpace\":{\"status\":\"UP\",\"details\":{\"total\":511555137536,\"free\":470637289472,\"threshold\":10485760,\"exists\":true}},\"mail\":{\"status\":\"UP\",\"details\":{\"location\":\"smtp.gmail.com:587\"}},\"ping\":{\"status\":\"UP\"}}}";

        //when
        String requestResult = template.requestBodyAndHeader("{{healthRouteStart}}", statusMessage, "env", "mock", String.class);

        //then
        assertThat(requestResult).isEqualTo(statusMessage);
        then(mailProcessor).shouldHaveNoInteractions();
    }

    @Test
    void testHealthRoute_whenFail() throws Exception {
        //given
        String statusMessage = "{\"status\":\"DOWN\",\"components\":{\"camelHealth\":{\"status\":\"UP\",\"details\":{\"name\":\"camel-health-check\",\"context\":\"UP\",\"route:healthRoute\":\"UP\",\"route:mainRoute\":\"UP\"}},\"db\":{\"status\":\"DOWN\",\"details\":{\"error\":\"org.springframework.jdbc.CannotGetJdbcConnectionException: Failed to obtain JDBC Connection; nested exception is org.postgresql.util.PSQLException: Подсоединение по адресу localhost:54321 отклонено. Проверьте что хост и порт указаны правильно и что postmaster принимает TCP/IP-подсоединения.\"}},\"diskSpace\":{\"status\":\"UP\",\"details\":{\"total\":511555137536,\"free\":470703751168,\"threshold\":10485760,\"exists\":true}},\"mail\":{\"status\":\"UP\",\"details\":{\"location\":\"smtp.gmail.com:587\"}},\"ping\":{\"status\":\"UP\"}}}";
        String expectedMessage = "db component in the route is down";

        //when
        String requestResult = template.requestBodyAndHeader("{{healthRouteStart}}", statusMessage, "env", "mock", String.class);

        //then
        assertThat(requestResult).isEqualTo(expectedMessage);
        then(mailProcessor).should().process(any(Exchange.class));
    }
}