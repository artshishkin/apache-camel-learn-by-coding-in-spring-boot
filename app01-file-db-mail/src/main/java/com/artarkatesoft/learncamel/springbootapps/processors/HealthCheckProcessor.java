package com.artarkatesoft.learncamel.springbootapps.processors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

@Component
@Slf4j
@RequiredArgsConstructor
public class HealthCheckProcessor implements Processor {

    private final ObjectMapper objectMapper;

    @Override
    public void process(Exchange exchange) throws Exception {
        String healthCheckResult = exchange.getIn().getBody(String.class);

        log.info("Health Check String of APP is `{}`", healthCheckResult);

        Map<String, Object> healthMap = objectMapper.readValue(healthCheckResult, new TypeReference<Map<String, Object>>() {
        });

        log.info("Health map is: {}", healthMap);

        StringJoiner joiner = new StringJoiner("\n");

        appendDownComponent(null, healthMap, joiner);

        if (joiner.length() > 0) {
            String healthErrorMessage = joiner.toString();
            log.info("Exception message is: {}", healthErrorMessage);
            exchange.getIn().setBody(healthErrorMessage);
            exchange.getIn().setHeader("error", true);
            RuntimeException runtimeException = new RuntimeException(healthErrorMessage);
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT, runtimeException);
        }
    }

    private void appendDownComponent(String parentKey, Map<String, Object> parentHealthMap, StringJoiner joiner) {
        if (parentHealthMap == null) return;
        for (Map.Entry<String, Object> entry : parentHealthMap.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if ("DOWN".equals(String.valueOf(value))) {
                if (parentKey != null)
                    joiner.add((key.equals("status") ? parentKey : key).concat(" component in the route is down"));
            } else {
                if (value instanceof LinkedHashMap){
                    Map<String, Object> childHealthMap = (Map<String, Object>) value;
                    appendDownComponent(key, childHealthMap, joiner);
                }
//                try {
//                    Map<String, Object> childHealthMap = objectMapper.readValue(objectMapper.writeValueAsString(value), new TypeReference<Map<String, Object>>() {
//                    });
//                    appendDownComponent(key, childHealthMap, joiner);
//                } catch (JsonProcessingException ignored) {
//                }
            }
        }
    }
}

