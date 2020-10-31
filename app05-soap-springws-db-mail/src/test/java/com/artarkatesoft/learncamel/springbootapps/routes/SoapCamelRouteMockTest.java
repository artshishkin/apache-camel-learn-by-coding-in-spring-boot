package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@CamelSpringBootTest
@SpringBootTest
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints
@TestPropertySource(properties = {
        "routeFromUri=direct:input",
        "errorRoute=mock:outputError"
})
class SoapCamelRouteMockTest {

    @Produce("{{routeFromUri}}")
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:log:sqlLog")
    MockEndpoint mockLogSqlEndpoint;

    @BeforeEach
    void setUp() {
        mockLogSqlEndpoint.expectedMessageCount(1);
    }

    @Test
    @DisplayName("Receive soap info from web service")
    void receiveAddMessageTest() throws InterruptedException {
        //given
        String soapMessageSend = "<FullCountryInfo xmlns=\"http://www.oorsprong.org/websamples.countryinfo\">\n" +
                "      <sCountryISOCode>GB</sCountryISOCode>\n" +
                "    </FullCountryInfo>";

        //when
        producerTemplate.sendBody(soapMessageSend);

        //then
//        mockLogSqlEndpoint.assertIsSatisfied();
        MockEndpoint.assertIsSatisfied(2, TimeUnit.SECONDS, mockLogSqlEndpoint);
        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(0);
//        String receiveBody = exchange.getIn().getBody(String.class);
//        assertThat(receiveBody).containsIgnoringCase("United Kingdom");
        Country receiveBody = exchange.getIn().getBody(Country.class);
        assertThat(receiveBody.getName()).isEqualToIgnoringCase("United Kingdom");
        assertThat(receiveBody.getCountryCode()).isEqualToIgnoringCase("GB");
    }




}