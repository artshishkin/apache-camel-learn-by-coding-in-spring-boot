package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.converter.jaxb.JaxbDataFormat;
import org.apache.camel.support.builder.Namespaces;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class SoapCamelRoute extends RouteBuilder {

    @Override
    public void configure() throws Exception {

        JaxbDataFormat dataFormat = new JaxbDataFormat();
        dataFormat.setContext(JAXBContext.newInstance(Country.class));
        dataFormat.setNamespacePrefix(Collections.singletonMap("m", "http://www.oorsprong.org/websamples.countryinfo"));

//        Map<String, String> aliases = new HashMap<>();
//        aliases.put("FullCountryInfoResult", Country.class.getName());
////        aliases.put("sName", "name");
////        aliases.put("sISOCode", "countryCode");
//
//        dataFormat.setAliases(aliases);
//        dataFormat.setPermissions(Country.class.getName());
//        dataFormat.

        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .to("{{routeTo1Uri}}")
                .log("The country SOAP response is ${body}")
//                .transform().xpath("/m:FullCountryInfoResponse/m:FullCountryInfoResult/m:sName/text()", new Namespaces("m", "http://www.oorsprong.org/websamples.countryinfo"))
                .transform().xpath("/m:FullCountryInfoResponse/m:FullCountryInfoResult", new Namespaces("m", "http://www.oorsprong.org/websamples.countryinfo"))
                .log("FullCountryInfoResult is \n${body}")

//                .convertBodyTo(String.class)
                .unmarshal(dataFormat)
                .log("Unmarshalled object is ${body}")
                .to("{{routeTo3Uri}}")
        ;
    }
}
