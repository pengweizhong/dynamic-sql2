package com.dynamic.sql.testing.unit;

import com.dynamic.sql.plugins.conversion.attribute.YearAttributeConverter;
import com.dynamic.sql.utils.ConverterUtils;
import java.sql.Date;
import java.time.Year;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


/** 年份转换回归，覆盖驱动可能返回的日期、数字与空值。 */
class YearConversionTest {
    @Test void writesNumericYearInsteadOfSerializedObject() {
        assertEquals(1901,new YearAttributeConverter().convertToDatabaseColumn(Year.of(1901)));
        assertEquals(2155,new YearAttributeConverter().convertToDatabaseColumn(Year.of(2155)));
    }
    @Test void acceptsJdbcDateAndNumericYears() {
        assertEquals(Year.of(1901),ConverterUtils.convertToEntityAttribute(null,Year.class,Date.valueOf("1901-01-01")));
        assertEquals(Year.of(2155),ConverterUtils.convertToEntityAttribute(null,Year.class,2155));
        assertEquals(Year.of(0),ConverterUtils.convertToEntityAttribute(null,Year.class,0));
    }
    @Test void preservesNull() {
        assertNull(new YearAttributeConverter().convertToDatabaseColumn(null));
        assertNull(ConverterUtils.convertToEntityAttribute(null,Year.class,null));
    }
}
