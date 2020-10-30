package com.artarkatesoft.learncamel.springbootapps.domain;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.time.LocalDateTime;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "FullCountryInfoResult",namespace = "http://www.oorsprong.org/websamples.countryinfo")
//@XmlRootElement(name = "FullCountryInfoResult")
//@XmlNs(prefix = "m",namespaceURI = "http://www.oorsprong.org/websamples.countryinfo")
@XmlType(name="", propOrder={"name","countryCode"})
public class Country {
    @XmlTransient
    private Long countryId;
    @NotEmpty
//    @XmlElement(name = "sName")
    @XmlAttribute(name = "sName")
    private String name;
//    private String sName;
    @Size(min = 2, max = 2)
//    @XmlElement(name = "sISOCode")
    @XmlAttribute(name = "sISOCode")
    private String countryCode;
//    private String sISOCode;

//    @XmlElement(name = "sName")
//    public String getName() {
//        return name;
//    }
//    @XmlElement(name = "sISOCode")
//    public String getCountryCode() {
//        return countryCode;
//    }

    @XmlTransient
    //    CREATE_TS
    private LocalDateTime created;
}
