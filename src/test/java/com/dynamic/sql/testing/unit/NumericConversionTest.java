package com.dynamic.sql.testing.unit;

import com.dynamic.sql.utils.ConverterUtils;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class NumericConversionTest {
    @Test void jdbcIntegerConvertsToSmallIntegerFields() {
        assertEquals(Short.valueOf((short)-32768),ConverterUtils.convertToEntityAttribute(null,Short.class,-32768));
        assertEquals(Short.valueOf((short)32767),ConverterUtils.convertToEntityAttribute(null,short.class,32767));
        assertEquals(Byte.valueOf((byte)-128),ConverterUtils.convertToEntityAttribute(null,Byte.class,-128));
        assertEquals(Byte.valueOf((byte)127),ConverterUtils.convertToEntityAttribute(null,byte.class,127));
    }
    @Test void outOfRangeValuesAreRejectedInsteadOfTruncated() {
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Short.class,32768));
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Short.class,-32769));
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Byte.class,128));
        assertThrows(NumberFormatException.class,()->ConverterUtils.convertToEntityAttribute(null,Byte.class,-129));
    }
    @Test void nullRemainsNull() {
        assertNull(ConverterUtils.convertToEntityAttribute(null,Short.class,null));
        assertNull(ConverterUtils.convertToEntityAttribute(null,Byte.class,null));
    }
}
