package com.dynamic.sql.plugins.conversion.attribute;

import com.dynamic.sql.plugins.conversion.AttributeConverterModel;
import java.sql.Date;
import java.time.Year;


/**
 * 年份与数据库数值之间的转换，避免 JDBC 将 Year 当成普通 Java 对象序列化。
 */
public class YearAttributeConverter extends AttributeConverterModel<Year> {
    @Override
    public Object convertToDatabaseColumn(Year attribute) {
        return attribute == null ? null : attribute.getValue();
    }

    @Override
    public Year convertToEntityAttribute(Object dbData) {
        if (dbData == null) return null;
        //MySQL 驱动默认将 YEAR 返回为 java.sql.Date，也可能按连接配置返回数值。
        if (dbData instanceof Date) {
            return Year.of(((Date) dbData).toLocalDate().getYear());
        }
        return Year.of(Integer.parseInt(dbData.toString()));
    }
}
