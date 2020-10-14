package com.artarkatesoft.learncamel.app01filedbmail.routes;

import org.apache.camel.Exchange;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.test.spring.junit5.CamelSpringBootTest;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

@CamelSpringBootTest
@SpringBootTest
@ActiveProfiles("dev")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SimpleRouteIT {

    private static final Path OUT_DIR_PATH = Path.of("data/output");
    private static final Path IN_DIR_PATH = Path.of("data/input");

    @Autowired
    ProducerTemplate template;

    @Value("${routeFromUri}")
    String routeFromUri;

    @BeforeEach
    void setUp() throws IOException {
        FileUtils.deleteDirectory(IN_DIR_PATH.toFile());
        FileUtils.deleteDirectory(OUT_DIR_PATH.toFile());
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
        assertThat(IN_DIR_PATH.resolve(fileName)).doesNotExist();
        assertThat(OUT_DIR_PATH.resolve(fileName)).exists();
    }
}
