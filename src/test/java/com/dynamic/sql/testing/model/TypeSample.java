package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.*;
import lombok.Data;

/**
 * 通用字段类型测试实体，物理数据库类型由各方言脚本定义。
 */
@Data
@Table("ds_type_sample")
public class TypeSample {
    @Id
    //样本固定主键，各数据库脚本保持相同场景编号。
    private Long id;
    //样本场景名称，用于识别空值、边界和普通数据。
    private String sampleName;
    //SMALLINT 边界值。
    private Short smallValue;
    //INT 边界值。
    private Integer intValue;
    //BIGINT 边界及超过浮点精确表示范围的整数。
    private Long longValue;
    //高精度定点数，读取时保留精度和小数位。
    private java.math.BigDecimal decimalValue;
    //单精度浮点数，测试采用容差比较。
    private Float floatValue;
    //双精度浮点数，测试采用容差比较。
    private Double doubleValue;
    //可空数值标志，用于区分 NULL、0 和 1。
    private Short flagValue;
    //定长字符，填充语义需要按数据库能力验证。
    private String charValue;
    //可变长文本，覆盖中文、emoji 和特殊字符。
    private String varcharValue;
    //长文本，包含换行符。
    private String textValue;
    //定长二进制，验证零字节及长度补齐。
    private byte[] binaryValue;
    //可变长二进制，区分空数组与 NULL。
    private byte[] varbinaryValue;
    //二进制大字段，验证原始字节不被转码。
    private byte[] blobValue;
    //日期，覆盖闰日和日期边界。
    private java.time.LocalDate dateValue;
    //一天内的时间，使用 LocalTime 验证 TIME(6) 微秒。
    private java.time.LocalTime timeValue;
    //无时区日期时间，保持数据库原始字段值。
    private java.time.LocalDateTime datetimeValue;
    //映射为数据库会话中的本地时间，不将其当成 UTC Instant。
    private java.time.LocalDateTime timestampValue;
    //数据库默认值，验证选择性插入时未指定列的行为。
    private String defaultValue;
}
