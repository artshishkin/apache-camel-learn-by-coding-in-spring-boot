package com.artarkatesoft.learncamel.app01filedbmail.processors;

import com.artarkatesoft.learncamel.app01filedbmail.domain.Item;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class BuildSQLProcessor implements Processor {
    @Override
    public void process(Exchange exchange) throws Exception {
        Item item = exchange.getMessage().getBody(Item.class);
        String transactionType = item.getTransactionType();
        StringBuilder query = new StringBuilder();
        switch (transactionType) {
            case "ADD":
                query.append("INSERT INTO items (sku, items_description, price) VALUES ('")
                        .append(item.getSku())
                        .append("','")
                        .append(item.getItemDescription())
                        .append("',")
                        .append(item.getPrice())
                        .append(");");
                break;
            case "UPDATE":
                break;
            case "DELETE":
                break;

        }
        log.info("Final Query is {}", query);
        exchange.getIn().setBody(query.toString());
    }
}
