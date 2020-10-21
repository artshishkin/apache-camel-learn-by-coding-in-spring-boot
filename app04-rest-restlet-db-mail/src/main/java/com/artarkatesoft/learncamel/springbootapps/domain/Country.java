package com.artarkatesoft.learncamel.springbootapps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    private Long countryId;
    private String name;
    @JsonProperty("alpha2Code")
    private String countryCode;
    private Long population;
//    CREATE_TS
    private LocalDateTime created;
}
