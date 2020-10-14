package com.artarkatesoft.learncamel.app01filedbmail.routes;

import com.artarkatesoft.learncamel.app01filedbmail.helper.Helper;
import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
//@TestPropertySource(properties = {""})
class SimpleRouteIT {

    public static final Path DIR_PATH = Path.of("data/output");

    @Autowired
    ProducerTemplate template;

    @Value("${routeFromUri}")
    String routeFromUri;

    @BeforeEach
    void setUp() throws IOException {
        Helper.deleteDirectory(DIR_PATH);
    }

    @Test
    void testMoveFile() throws InterruptedException {
        //given
        String fileContent = "type,sku#,item_description,price\n" +
                "ADD,100,Samsung TV,500\n" +
                "ADD,101,LG TV,300";
        String fileName = "fileTest.txt";

        //when
        template.sendBodyAndHeader(routeFromUri, fileContent, Exchange.FILE_NAME, fileName);

        //then
        Thread.sleep(3000);
        assertThat(DIR_PATH.resolve(fileName)).exists();
    }
}
