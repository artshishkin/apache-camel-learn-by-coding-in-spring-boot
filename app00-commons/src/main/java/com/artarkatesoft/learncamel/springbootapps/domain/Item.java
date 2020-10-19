package com.artarkatesoft.learncamel.springbootapps.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@CsvRecord(separator = ",", skipFirstLine = true)
@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class Item {
//    type,sku#,item_description,price
//    ADD,100,Samsung TV,500
//    ADD,101,LG TV,300

    @NotEmpty
    @DataField(pos = 1)
    private String transactionType;

    @NotEmpty
    @DataField(pos = 2)
    private String sku;

    @NotEmpty
    @Size(min = 3, max = 255)
    @DataField(pos = 3)
    private String itemDescription;

    @Positive
    @DataField(pos = 4, precision = 2)
    private BigDecimal price;
}
