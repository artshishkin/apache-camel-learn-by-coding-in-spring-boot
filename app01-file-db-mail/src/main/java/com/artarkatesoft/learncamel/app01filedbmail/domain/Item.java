package com.artarkatesoft.learncamel.app01filedbmail.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.camel.dataformat.bindy.annotation.CsvRecord;
import org.apache.camel.dataformat.bindy.annotation.DataField;

import java.math.BigDecimal;

@CsvRecord(separator = ",", skipFirstLine = true)
@NoArgsConstructor
@Data
public class Item {
//    type,sku#,item_description,price
//    ADD,100,Samsung TV,500
//    ADD,101,LG TV,300

    @DataField(pos = 1)
    private String transactionType;
    @DataField(pos = 2)
    private String sku;
    @DataField(pos = 3)
    private String itemDescription;
    @DataField(pos = 4, precision = 2)
    private BigDecimal price;
}
