package com.artarkatesoft.learncamel.app02kafkadbmail.routes;

import com.artarkatesoft.learncamel.app00commons.domain.Item;
import org.apache.camel.CamelExecutionException;
import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.component.bean.validator.BeanValidationException;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("mock")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaRouteMockTest {

    @Produce("{{routeFromUri}}")
    ProducerTemplate producerTemplate;

    @EndpointInject("{{routeTo1Uri}}")
    private MockEndpoint mockEndpoint;

    @EndpointInject("{{errorRoute}}")
    private MockEndpoint mockErrorEndpoint;

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


    @ParameterizedTest
    @ValueSource(strings = {
            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"SamsungTV\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"\", \"itemDescription\":\"\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"XX\", \"price\":\"500.00\"}",
            "{\"transactionType\":\"ADD\", \"sku\":\"666\", \"itemDescription\":\"Hell TV\", \"price\":\"-666.00\"}"
    })
    void unmarshallItemTest_validationError(String kafkaJsonMessage) throws InterruptedException {
        //given
        mockEndpoint.expectedMessageCount(0);
        mockErrorEndpoint.expectedMessageCount(1);

        //when
        ThrowableAssert.ThrowingCallable thrown = () -> producerTemplate.sendBody(kafkaJsonMessage);

        //then
        assertThatThrownBy(thrown)
                .isInstanceOf(CamelExecutionException.class)
                .hasCauseInstanceOf(BeanValidationException.class);
//                .hasMessageContaining("Validation failed for: Item(");

        mockEndpoint.assertIsSatisfied();
        mockErrorEndpoint.assertIsSatisfied();
    }

}