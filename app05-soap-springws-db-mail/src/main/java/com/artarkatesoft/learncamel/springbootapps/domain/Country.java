package com.artarkatesoft.learncamel.springbootapps.domain;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAliasType;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@XStreamAliasType("FullCountryInfoResult")
public class Country {
    @XStreamOmitField
    private Long countryId;
    @NotEmpty
    @XStreamAlias("sName")
    private String name;
    @Size(min = 2, max = 2)
    @XStreamAlias("sISOCode")
    private String countryCode;
    @XStreamOmitField
    private LocalDateTime created;

    private String sCapitalCity;
    private Integer sPhoneCode;
    private String sContinentCode;
    private String sCurrencyISOCode;
    private String sCountryFlag;
    private String Languages;
}
