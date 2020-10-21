package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.alert.MailProcessor;
import com.artarkatesoft.learncamel.springbootapps.domain.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints("log:*")
@MockEndpointsAndSkip("{{errorRoute}}")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "spring.activemq.broker-url=vm://localhost",

        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.schema=classpath:schema.sql"
})
class ActiveMQRouteIT {

    @Produce("{{routeFromUri}}")
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:log:sqlLog")
    MockEndpoint mockLogSqlEndpoint;

    @EndpointInject("mock:{{errorRoute}}")
    MockEndpoint mockErrorRouteEndpoint;

    @Autowired
    ConsumerTemplate consumerTemplate;

    @Autowired
    CamelContext camelContext;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MailProcessor mailProcessor;

    @Captor
    ArgumentCaptor<Exchange> exchangeCaptor;

    @BeforeEach
    void setUp(){
        mockLogSqlEndpoint.expectedMessageCount(1);
    }

    @Test
    @Order(10)
    @DisplayName("First fake send message (WTF)")
    void receiveAddMessageTestFake() throws InterruptedException {
        //given
        String jsonMessage = "{\"transactionType\":\"ADD\", \"sku\":\"123\", \"itemDescription\":\"WTF TV\", \"price\":\"50.00\"}";

        //when
        producerTemplate.sendBody(jsonMessage);
    }

    @Test
    @Order(11)
    @DisplayName("Received ActiveMQ ADD message should be persisted in DB")
    void receiveAddMessageTest() throws InterruptedException {
        //given
        String jsonMessage = "{\"transactionType\":\"ADD\", \"sku\":\"123\", \"itemDescription\":\"TestTV\", \"price\":\"500.00\"}";

        //when
        producerTemplate.sendBody(jsonMessage);

        //then
//        mockLogSqlEndpoint.assertIsSatisfied();
        MockEndpoint.assertIsSatisfied(2, TimeUnit.SECONDS, mockLogSqlEndpoint);
        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(0);
        String receiveBody = exchange.getIn().getBody(String.class);
        assertThat(receiveBody).containsIgnoringCase("SKU=123, ITEMS_DESCRIPTION=TestTV, PRICE=500.00, CREATE_TS=");
    }

    @Test
    @Order(12)
    @DisplayName("Received ActiveMQ UPDATE message should modify existed item in DB")
    void receiveUpdateMessageTest() throws InterruptedException {
        //given
        String jsonMessageUpdate = "{\"transactionType\":\"UPDATE\", \"sku\":\"123\", \"itemDescription\":\"TestNewTV\", \"price\":\"123.00\"}";

        //when
        producerTemplate.sendBody(jsonMessageUpdate);

        //then
        mockLogSqlEndpoint.assertIsSatisfied();

        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(0);
        String receiveBody = exchange.getIn().getBody(String.class);
        assertThat(receiveBody).containsIgnoringCase("SKU=123, ITEMS_DESCRIPTION=TestNewTV, PRICE=123.00, CREATE_TS=");
    }

    @Test
    @Order(13)
    @DisplayName("Received ActiveMQ DELETE message should delete item from DB")
    void receiveDeleteMessageTest() throws InterruptedException {
        //given
        String jsonMessageDelete = "{\"transactionType\":\"DELETE\", \"sku\":\"123\", \"itemDescription\":\"TestDelTV\", \"price\":\"500.00\"}";

        //when
        producerTemplate.sendBody(jsonMessageDelete);

        //then
        mockLogSqlEndpoint.assertIsSatisfied();

        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(0);
        String receiveBody = exchange.getIn().getBody(String.class);
        assertThat(receiveBody).isEmpty();
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"XX\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"Hell TV\", \"price\":\"-666.00\"}"
    })
    @DisplayName("Messages with Validation errors should be redirected to errorItemQueue")
    void activeMQTest_validationError(String jsonMessage) throws InterruptedException, JsonProcessingException {
        //given
        Item expectedItem = objectMapper.readValue(jsonMessage, Item.class);
        mockLogSqlEndpoint.expectedMessageCount(0);
        mockErrorRouteEndpoint.expectedMessageCount(1);
        //when
        producerTemplate.sendBody(jsonMessage);

        //then
//        String actualItem = consumerTemplate.receiveBody("{{errorRoute}}", 2000L, String.class);
        mockErrorRouteEndpoint.assertIsSatisfied();
        mockLogSqlEndpoint.assertIsSatisfied();
        Exchange exchange = mockErrorRouteEndpoint.assertExchangeReceived(0);
        String messageToErrorQueue = exchange.getIn().getBody(String.class);
        Item actualItem = objectMapper.readValue(messageToErrorQueue, Item.class);
        assertThat(actualItem).isEqualTo(expectedItem);
    }

    @Test
    @DisplayName("Messages with Wrong Transaction Type should be redirected to errorItemQueue and Mail")
    void activeMQTest_wrongTransactionType() throws Exception {
        //given
        String jsonMessage = "{\"transactionType\":\"NOT_IMPLEMENTED\", \"sku\":\"123\", \"itemDescription\":\"ABC TV\", \"price\":\"500.00\"}";
        Item expectedItem = objectMapper.readValue(jsonMessage, Item.class);
        mockLogSqlEndpoint.expectedMessageCount(0);
        mockErrorRouteEndpoint.expectedMessageCount(1);

        //when
        producerTemplate.sendBody(jsonMessage);
//        assertThatThrownBy(() -> producerTemplate.sendBody(jsonMessage))
//                .hasCauseInstanceOf(DataException.class)
//        ;

        //then
        mockErrorRouteEndpoint.assertIsSatisfied();
        mockLogSqlEndpoint.assertIsSatisfied();
        Exchange exchange = mockErrorRouteEndpoint.assertExchangeReceived(0);
        String messageToErrorQueue = exchange.getIn().getBody(String.class);
        Item actualItem = objectMapper.readValue(messageToErrorQueue, Item.class);

        assertThat(actualItem).isEqualTo(expectedItem);

        then(mailProcessor).should().process(exchangeCaptor.capture());
        Exchange mailProcessorExchange = exchangeCaptor.getValue();
        String mailedBody = mailProcessorExchange.getIn().getBody(String.class);
        Item mailedItem = objectMapper.readValue(mailedBody, Item.class);

        assertThat(mailedItem).isEqualTo(expectedItem);
    }

    @Test
    @DisplayName("Wrong Messages should be redirected to errorItemQueue and Mail")
    void activeMQTest_wrongMessage() throws Exception {
        //given
        String wrongMessage = "WTF";
        mockLogSqlEndpoint.expectedMessageCount(0);
        mockErrorRouteEndpoint.expectedMessageCount(1);

        //when
        producerTemplate.sendBody(wrongMessage);
//        assertThatThrownBy(() -> producerTemplate.sendBody(wrongMessage))
//                .hasCauseInstanceOf(JsonSyntaxException.class)
//        ;

        //then
        mockErrorRouteEndpoint.assertIsSatisfied();
        mockLogSqlEndpoint.assertIsSatisfied();
        Exchange exchange = mockErrorRouteEndpoint.assertExchangeReceived(0);
        String messageToErrorQueue = exchange.getIn().getBody(String.class);

        assertThat(messageToErrorQueue).isEqualTo("WTF");

        then(mailProcessor).should().process(exchangeCaptor.capture());
        Exchange mailProcessorExchange = exchangeCaptor.getValue();
        String mailedBody = mailProcessorExchange.getIn().getBody(String.class);

        assertThat(mailedBody).isEqualTo("WTF");
    }

}