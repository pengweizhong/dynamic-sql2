package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import lombok.Data;

@Data
@Table("ds_type_sample")
public class TypeSample {
    @Id
    private Long id;
    private String sampleName;
    private Short smallValue;
    private Integer intValue;
    private Long longValue;
    private java.math.BigDecimal decimalValue;
    private Float floatValue;
    private Double doubleValue;
    private Short flagValue;
    private String charValue;
    private String varcharValue;
    private String textValue;
    private byte[] binaryValue;
    private byte[] varbinaryValue;
    private byte[] blobValue;
    private java.time.LocalDate dateValue;
    private java.sql.Time timeValue;
    private java.time.LocalDateTime datetimeValue;
    private java.time.LocalDateTime timestampValue;
    private String defaultValue;
}
