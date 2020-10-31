package com.artarkatesoft.learncamel.springbootapps.routes;

import com.artarkatesoft.learncamel.springbootapps.domain.Country;
import com.artarkatesoft.learncamel.springbootapps.processors.RequestXmlBuildProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.dataformat.xstream.XStreamDataFormat;
import org.apache.camel.support.builder.Namespaces;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SoapCamelRoute extends RouteBuilder {

    private final RequestXmlBuildProcessor requestXmlBuildProcessor;

    @Override
    public void configure() throws Exception {
        XStreamDataFormat dataFormat = new XStreamDataFormat();
        Map<String, String> aliases = new HashMap<>();
        aliases.put("FullCountryInfoResult", Country.class.getName());

        dataFormat.setAliases(aliases);
        dataFormat.setPermissions(Country.class.getName());

//        <m:sISOCode>GB</m:sISOCode>
//        <m:sName>United Kingdom</m:sName>
//        <m:sCapitalCity>London</m:sCapitalCity>
//        <m:sPhoneCode>44</m:sPhoneCode>
//        <m:sContinentCode>EU</m:sContinentCode>
//        <m:sCurrencyISOCode>GBP</m:sCurrencyISOCode>
//        <m:sCountryFlag>http://www.oorsprong.org/WebSamples.CountryInfo/Flags/United_Kingdom.jpg</m:sCountryFlag>
//        <m:Languages>



        from("{{routeFromUri}}")
                .log("Evn. is `{{message}}`")
                .process(requestXmlBuildProcessor)
                .to("{{routeTo1Uri}}")
                .log("The country SOAP response is\n${body}")
                .transform().xpath("/m:FullCountryInfoResponse/m:FullCountryInfoResult", new Namespaces("m", "http://www.oorsprong.org/websamples.countryinfo"))
                .log("FullCountryInfoResult is \n${body}")

                .unmarshal(dataFormat)
                .log("Unmarshalled object is ${body}")
                .to("bean-validator://countryValidator")
//                .setHeader("countryId",simple("${body.countryCode}"))
                .setBody(simple("INSERT INTO countries (name, country_code) VALUES ('${body.name}','${body.countryCode}');"))
                .log("Final query is ${body}")
                .to("{{dbRoute}}")

                .to("{{routeTo3Uri}}")
        ;
    }
}
