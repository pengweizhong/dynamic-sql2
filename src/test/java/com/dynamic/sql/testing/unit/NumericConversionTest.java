package com.dynamic.sql.testing.unit;

import com.dynamic.sql.utils.ConverterUtils;
import java.math.BigInteger;
import java.sql.Time;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/**
 * 类型转换回归测试，重点检查边界、无符号数和 NULL 语义。
 */
class NumericConversionTest {
    /**
     * 验证 JDBC Integer 到 Short、Byte 的边界转换，同时覆盖基本类型和包装类型。
     */
    @Test void jdbcIntegerConvertsToSmallIntegerFields() {
        assertEquals(Short.valueOf((short)-32768),ConverterUtils.convertToEntityAttribute(null,Short.class,-32768));
        assertEquals(Short.valueOf((short)32767),ConverterUtils.convertToEntityAttribute(null,short.class,32767));
        assertEquals(Byte.valueOf((byte)-128),ConverterUtils.convertToEntityAttribute(null,Byte.class,-128));
        assertEquals(Byte.valueOf((byte)127),ConverterUtils.convertToEntityAttribute(null,byte.class,127));
    }
    /**
     * 超出目标整数范围时必须报错，不能通过强制窄化产生截断值。
     */
    @Test void outOfRangeValuesAreRejectedInsteadOfTruncated() {
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Short.class,32768));
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Short.class,-32769));
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Byte.class,128));
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Byte.class,-129));
    }
    /**
     * BIT(64) 全 1 应得到无符号最大值，不能误判为 -1。
     */
    @Test void bitBytesConvertToUnsignedBigInteger() {
        assertEquals(new BigInteger("18446744073709551615"),
                ConverterUtils.convertToEntityAttribute(null,BigInteger.class,new byte[]{-1,-1,-1,-1,-1,-1,-1,-1}));
        assertEquals(BigInteger.ZERO,
                ConverterUtils.convertToEntityAttribute(null,BigInteger.class,new byte[]{0}));
    }
    /**
     * 验证普通 JDBC Time 的兼容转换；微秒精度由 MySQL 集成用例单独检查。
     */
    @Test void legacyJdbcTimeCanConvertToLocalTime() {
        assertEquals(LocalTime.of(12,34,56),ConverterUtils.convertToEntityAttribute(
                null,LocalTime.class,Time.valueOf("12:34:56")));
    }
    /**
     * 包装类型接收 SQL NULL 时应保留 null。
     */
    @Test void nullRemainsNull() {
        assertNull(ConverterUtils.convertToEntityAttribute(null,Short.class,null));
        assertNull(ConverterUtils.convertToEntityAttribute(null,Byte.class,null));
    }
}
