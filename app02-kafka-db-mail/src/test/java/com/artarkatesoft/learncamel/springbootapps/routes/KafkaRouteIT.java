package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.domain.Item;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.*;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.schema: classpath:schema.sql",
        "spring.kafka.consumer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "routeFromUri=kafka:inputItemTopic?brokers=${spring.embedded.kafka.brokers}&groupId=groupItem&autoOffsetReset=earliest&consumersCount=1",
        "errorRoute=kafka:errorTopic?brokers=${spring.embedded.kafka.brokers}"
})
@EmbeddedKafka(topics = {"inputItemTopic", "errorTopic"}, partitions = 3)
@MockEndpoints("log:*")
class KafkaRouteIT {

    @Produce("{{routeFromUri}}")
    ProducerTemplate producerTemplate;

    @EndpointInject("mock:log:sqlLog")
    MockEndpoint mockLogSqlEndpoint;

    @Autowired
    ConsumerTemplate consumerTemplate;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("Received Kafka ADD message should be persisted in DB")
    void receiveAddMessageTest() throws InterruptedException {
        //given
        String kafkaJsonMessage = "{\"transactionType\":\"ADD\", \"sku\":\"123\", \"itemDescription\":\"TestTV\", \"price\":\"500.00\"}";
        mockLogSqlEndpoint.expectedMessageCount(1);

        //when
        producerTemplate.sendBody(kafkaJsonMessage);

        //then
        mockLogSqlEndpoint.assertIsSatisfied();

        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(0);
        String receiveBody = exchange.getIn().getBody(String.class);
        assertThat(receiveBody).contains("ITEM_I=1, SKU=123, ITEMS_DESCRIPTION=TestTV, PRICE=500.00, CREATE_TS=");
    }

    @Test
    @DisplayName("Received Kafka UPDATE message should modify existed item in DB")
    void receiveUpdateMessageTest() throws InterruptedException {
        //given
        String kafkaJsonMessageAdd = "{\"transactionType\":\"ADD\", \"sku\":\"123\", \"itemDescription\":\"TestTV\", \"price\":\"500.00\"}";
        String kafkaJsonMessageUpdate = "{\"transactionType\":\"UPDATE\", \"sku\":\"123\", \"itemDescription\":\"TestNewTV\", \"price\":\"123.00\"}";

        mockLogSqlEndpoint.expectedMessageCount(2);

        //when
        producerTemplate.sendBody(kafkaJsonMessageAdd);
        Thread.sleep(100);
        producerTemplate.sendBody(kafkaJsonMessageUpdate);

        //then
        mockLogSqlEndpoint.assertIsSatisfied();

        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(1);
        String receiveBody = exchange.getIn().getBody(String.class);
        assertThat(receiveBody).contains("ITEM_I=1, SKU=123, ITEMS_DESCRIPTION=TestNewTV, PRICE=123.00, CREATE_TS=");
    }

    @Test
    @DisplayName("Received Kafka DELETE message should delete item from DB")
    void receiveDeleteMessageTest() throws InterruptedException {
        //given
        String kafkaJsonMessageAdd = "{\"transactionType\":\"ADD\", \"sku\":\"123\", \"itemDescription\":\"TestTV\", \"price\":\"500.00\"}";
        String kafkaJsonMessageDelete = "{\"transactionType\":\"DELETE\", \"sku\":\"123\", \"itemDescription\":\"TestDelTV\", \"price\":\"500.00\"}";

        mockLogSqlEndpoint.expectedMessageCount(2);

        //when
        producerTemplate.sendBody(kafkaJsonMessageAdd);
        Thread.sleep(100);
        producerTemplate.sendBody(kafkaJsonMessageDelete);

        //then
        mockLogSqlEndpoint.assertIsSatisfied();

        Exchange exchange = mockLogSqlEndpoint.assertExchangeReceived(1);
        String receiveBody = exchange.getIn().getBody(String.class);
        assertThat(receiveBody).isEmpty();
    }


    @ParameterizedTest
    @ValueSource(strings = {
            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"\", \"price\":\"500.00\"}",
//            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"\", \"price\":\"500.00\"}",
//            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"XX\", \"price\":\"500.00\"}",
//            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"Hell TV\", \"price\":\"-666.00\"}"
    })
//    @ValueSource(strings = {
//            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}"
//    })
    void kafkaTest_validationError(String kafkaJsonMessage) throws InterruptedException, JsonProcessingException {
        //given
        Item expectedItem = objectMapper.readValue(kafkaJsonMessage, Item.class);
        mockLogSqlEndpoint.expectedMessageCount(0);

        //when
        producerTemplate.sendBody(kafkaJsonMessage);

        //then
        mockLogSqlEndpoint.assertIsSatisfied();
        String actualItem = consumerTemplate.receiveBody("{{errorRoute}}", String.class);
        assertThat(actualItem).isEqualTo(expectedItem.toString());
    }
}