package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import com.artarkatesoft.learncamel.springbootapps.domain.CountryResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.rest.RestBindingMode;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CountryRestRoute extends RouteBuilder {
    @Override
    public void configure() throws Exception {
        restConfiguration()
                .component("servlet")
//                .host("localhost").port(8080)
                // Allow Camel to try to marshal/unmarshal between Java objects and JSON
                .bindingMode(RestBindingMode.auto);


//        rest("/countries").get("/").to("log:apiLog?showAll=true");


//        from("rest:get:api/{countryCode}")
//                .log("get request. body is `${body}`, Headers are: `${headers}`")
//                .transform().simple("CODE IS ${headers.countryCode}");

        // Now define the REST API (POST, GET, etc.)
        rest()
                .path("/api") // This makes the API available at http://host:port/$CONTEXT_ROOT/api

                .consumes("application/json")
                .produces("application/json")

                // HTTP: GET /api
                .get("{countryCode}")
                    .route()
                        .to("log:meLog?showAll=true")
                        .transform(simple("Country Code is ${headers.countryCode}"))
                    .endRest()
                .get("/country/{countryCode}")
                    .route()
                        .to("log:meLog?showAll=true")
                        .transform(simple("Code is ${headers.countryCode}"))
                    .endRest()

//
//                .outType(ResponseType.class) // Setting the response type enables Camel to marshal the response to JSON
//                .to("bean:getBean") // This will invoke the Spring bean 'getBean'
//
//                // HTTP: POST /api
//                .post()
//                .type(PostRequestType.class) // Setting the request type enables Camel to unmarshal the request to a Java object
//                .outType(ResponseType.class) // Setting the response type enables Camel to marshal the response to JSON
//                .to("bean:postBean")
                .post("/countries").type(Country.class)
                    .route()
                    .to("log:meLog?showAll=true")

                    .process(exchange -> {
                        Country country = exchange.getIn().getBody(Country.class);
                        exchange.getIn().setBody(new CountryResponse(
                                String.format("Received country is %s",country.getName())
                        ));
                    })
//                    .transform(simple("Received ${body}"))

                .endRest()
        ;



    }
}