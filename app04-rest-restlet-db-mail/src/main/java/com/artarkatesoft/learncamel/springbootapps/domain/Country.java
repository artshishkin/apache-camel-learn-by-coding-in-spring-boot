package com.artarkatesoft.learncamel.springbootapps.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Country {
    private Long countryId;
    @NotEmpty
    private String name;
//    @JsonProperty("alpha2Code")
    @Size(min = 2, max = 2)
//    private String countryCode;
    private String alpha2Code;
    @Positive
    private Long population;
    //    CREATE_TS
    private LocalDateTime created;
}
