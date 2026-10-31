package com.dynamic.sql.testing.model;

import com.dynamic.sql.anno.Id;
import com.dynamic.sql.anno.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;
import lombok.Data;


/**
 * MySQL 专属可写类型实体；生成列不作为可写属性，使用 JDBC 独立核对其结果。
 */
@Data
@Table("ds_mysql_type_sample")
public class MysqlTypeSample {
    //测试记录主键，事务结束后回滚。
    @Id
    private Long id;
    //有符号 TINYINT。
    private Byte tinyValue;
    //TINYINT(1) 的数值语义。
    private Integer tinyBoolean;
    //有符号 MEDIUMINT。
    private Integer mediumValue;
    //无符号 TINYINT。
    private Integer unsignedTiny;
    //无符号 SMALLINT。
    private Integer unsignedSmall;
    //无符号 MEDIUMINT。
    private Integer unsignedMedium;
    //无符号 INT。
    private Long unsignedInt;
    //无符号 BIGINT。
    private BigInteger unsignedBig;
    //单比特值。
    private Integer bitFlag;
    //八位位串。
    private byte[] bitMask;
    //六十四位位串。
    private byte[] bitWide;
    //年份，不作为完整日期处理。
    private Year yearValue;
    //MySQL 时长，允许负数及超过一天。
    private String durationValue;
    //数据库枚举文本。
    private String enumValue;
    //数据库集合文本。
    private String setValue;
    //JSON 文本，以结构等价核对。
    private String jsonValue;
    //短文本。
    private String tinyText;
    //中等文本。
    private String mediumText;
    //长文本。
    private String longText;
    //短二进制。
    private byte[] tinyBlob;
    //中等二进制。
    private byte[] mediumBlob;
    //长二进制。
    private byte[] longBlob;
    //生成列表达式的输入值。
    private BigDecimal baseAmount;
}
