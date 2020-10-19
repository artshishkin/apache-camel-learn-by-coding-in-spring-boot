package com.artarkatesoft.learncamel.app02kafkadbmail.routes;

import com.artarkatesoft.learncamel.app00commons.domain.Item;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaRouteMockTest {

    @Produce("{{routeFromUri}}")
    ProducerTemplate producerTemplate;

    @EndpointInject("{{routeTo1Uri}}")
    private MockEndpoint mockEndpoint;

    @Test
    void unmarshallItemTest() throws InterruptedException {
        //given
        String kafkaJsonMessage = "{\"transactionType\":\"ADD\", \"sku\":\"101\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}";
        Item expectedItem = Item.builder()
                .transactionType("ADD")
                .sku("101")
                .itemDescription("SamsungTV")
                .price(BigDecimal.valueOf(50000L, 2))
                .build();

        mockEndpoint.expectedMessageCount(1);
        mockEndpoint.expectedBodiesReceived(expectedItem);

        //when
        producerTemplate.sendBody(kafkaJsonMessage);

        //then
        mockEndpoint.assertIsSatisfied();


    }
}