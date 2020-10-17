package com.artarkatesoft.learncamel.app01filedbmail.processors;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

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

        StringJoiner joiner = null;

        for (Map.Entry<String, Object> entry : healthMap.entrySet()) {
            String component = entry.getKey();
            String valueStr = entry.getValue().toString();
            if (valueStr.contains("DOWN")) {
                if (joiner == null)
                    joiner = new StringJoiner("\n");
                joiner.add(component + " component in the route is down");
            }
        }

        if (joiner != null) {
            String healthErrorMessage = joiner.toString();
            log.info("Exception message is: {}", healthErrorMessage);
            exchange.getIn().setBody(healthErrorMessage);
            exchange.getIn().setHeader("error", true);
            RuntimeException runtimeException = new RuntimeException(healthErrorMessage);
            exchange.setProperty(Exchange.EXCEPTION_CAUGHT, runtimeException);
        }
    }
}

