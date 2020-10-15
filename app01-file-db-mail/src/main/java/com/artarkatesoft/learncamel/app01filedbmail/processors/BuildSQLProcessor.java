package com.artarkatesoft.learncamel.app01filedbmail.processors;

import com.artarkatesoft.learncamel.app01filedbmail.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildSQLProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = exchange.getMessage().getBody(Item.class);
        String transactionType = item.getTransactionType();
        String query = null;
        Message message = exchange.getIn();
        switch (transactionType) {
            case "ADD":
                message.setHeader("sku", item.getSku());
                message.setHeader("itemDescription", item.getItemDescription());
                message.setHeader("itemPrice", item.getPrice());
                query = "INSERT INTO items (sku, items_description, price) VALUES (:?sku,:?itemDescription,:?itemPrice);";
                break;
            case "UPDATE":
                message.setHeader("sku", item.getSku());
                message.setHeader("itemDescription", item.getItemDescription());
                message.setHeader("itemPrice", item.getPrice());
                query = "UPDATE items SET (items_description, price) = ( :?itemDescription, :?itemPrice ) WHERE sku = :?sku;";
                break;
            case "DELETE":
                break;
        }
        log.info("Final Query is {} with headers {}", query, message.getHeaders());
        message.setBody(query);
    }
}
