package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.camel.test.spring.junit5.MockEndpoints;
import org.apache.camel.test.spring.junit5.MockEndpointsAndSkip;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@CamelSpringBootTest
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@MockEndpoints("log:*")
@MockEndpointsAndSkip("{{routeFromUri}}")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.datasource.schema=classpath:schema.sql"
})
class CountryRestRouteRestTemplateTest {

    @Autowired
    TestRestTemplate restTemplate;

    // Spring will inject the random port assigned to the web server
    @LocalServerPort
    int webServerPort;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void postTest() throws JsonProcessingException {
        //given
        String payload = "{\"name\":\"Italy\",\"topLevelDomain\":[\".it\"],\"alpha2Code\":\"IT\",\"alpha3Code\":\"ITA\",\"callingCodes\":[\"39\"],\"capital\":\"Rome\",\"altSpellings\":[\"IT\",\"Italian Republic\",\"Repubblica italiana\"],\"region\":\"Europe\",\"subregion\":\"Southern Europe\",\"population\":60665551,\"latlng\":[42.83333333,12.83333333],\"demonym\":\"Italian\",\"area\":301336.0,\"gini\":36.0,\"timezones\":[\"UTC+01:00\"],\"borders\":[\"AUT\",\"FRA\",\"SMR\",\"SVN\",\"CHE\",\"VAT\"],\"nativeName\":\"Italia\",\"numericCode\":\"380\",\"currencies\":[{\"code\":\"EUR\",\"name\":\"Euro\",\"symbol\":\"€\"}],\"languages\":[{\"iso639_1\":\"it\",\"iso639_2\":\"ita\",\"name\":\"Italian\",\"nativeName\":\"Italiano\"}],\"translations\":{\"de\":\"Italien\",\"es\":\"Italia\",\"fr\":\"Italie\",\"ja\":\"イタリア\",\"it\":\"Italia\",\"br\":\"Itália\",\"pt\":\"Itália\",\"nl\":\"Italië\",\"hr\":\"Italija\",\"fa\":\"ایتالیا\"},\"flag\":\"https://restcountries.eu/data/ita.svg\",\"regionalBlocs\":[{\"acronym\":\"EU\",\"name\":\"European Union\",\"otherAcronyms\":[],\"otherNames\":[]}],\"cioc\":\"ITA\"}";
        Country country = objectMapper.readValue(payload, Country.class);
        String expectedResponsePart = "[{\"COUNTRY_I\":1,\"NAME\":\"Italy\",\"COUNTRY_CODE\":\"IT\",\"POPULATION\":60665551,\"CREATE_TS\":";

        //when
        String actualResponse = restTemplate.postForObject("http://localhost:" + webServerPort + "/services/api/countries", country, String.class);

        //then
        assertThat(actualResponse).containsIgnoringCase(expectedResponsePart);
    }
}