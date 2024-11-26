package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverterModel;
import com.pengwz.dynamic.sql2.plugins.conversion.attribute.BigDecimalAttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.attribute.LocalDateAttributeConverter;
import com.pengwz.dynamic.sql2.table.FieldMeta;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConverterUtils {
    //自定义转换器 搭配@Column注解使用
    private static final Map<Class<? extends AttributeConverter>, AttributeConverter> CUSTOM_ATTRIBUTE_CONVERTERS = new LinkedHashMap<>();
    private static final Map<Class<?>, AttributeConverterModel> GENERAL_ATTRIBUTE_CONVERTER_MODEL = new LinkedHashMap<>();

    private ConverterUtils() {
    }

    static {
        putAttributeConverterModel(BigDecimal.class, new BigDecimalAttributeConverter());
        putAttributeConverterModel(LocalDate.class, new LocalDateAttributeConverter());
    }

    public static <Y> AttributeConverter<Object, Y> loadCustomConverter(Class<? extends AttributeConverter> converterClass) {
        AttributeConverter attributeConverter = CUSTOM_ATTRIBUTE_CONVERTERS.get(converterClass);
        if (attributeConverter != null) {
            return attributeConverter;
        }
        AttributeConverter instance = ReflectUtils.instance(converterClass);
        CUSTOM_ATTRIBUTE_CONVERTERS.put(converterClass, instance);
        return instance;
    }

    public static Object convertToDatabaseColumn(FieldMeta fieldMeta, Object value) {
        if (value == null) {
            return null;
        }
        //如果该字段直接实现了转换器
        if (value instanceof AttributeConverter) {
            AttributeConverter attributeConverter = (AttributeConverter) value;
            return attributeConverter.convertToDatabaseColumn(value);
        }
        if (fieldMeta.getConverter() != null) {
            return loadCustomConverter(fieldMeta.getConverter()).convertToDatabaseColumn(value);
        }
        AttributeConverter<Object, Object> attributeConverter = GENERAL_ATTRIBUTE_CONVERTER_MODEL.get(fieldMeta.getField().getType());
        if (attributeConverter != null) {
            return attributeConverter.convertToDatabaseColumn(value);
        }
        // 如果没有找到适合的转换器，抛出异常或者返回默认值
        return value;
    }

    @SuppressWarnings("unchecked")
    public static <T, V> T convertToEntityAttribute(Class<T> fieldType, V value) {
        if (value == null) {
            return null;
        }
        //如果是字段本身实现了AttributeConverter
        if (value instanceof AttributeConverter) {
            AttributeConverter attributeConverter = (AttributeConverter) value;
            return (T) attributeConverter.convertToEntityAttribute(value);
        }
        Class<?> valueType = value.getClass();
        if (fieldType.isAssignableFrom(valueType)) {
            return (T) value;
        }
        // 检查是否有通用的转换器
        AttributeConverter<Object, V> attributeConverter = GENERAL_ATTRIBUTE_CONVERTER_MODEL.get(fieldType);
        if (attributeConverter != null) {
            return (T) attributeConverter.convertToEntityAttribute(value);
        }
        //处理一下特别常见的类型
        if (fieldType == String.class) {
            return (T) value.toString();
        }
        if (fieldType == Integer.class || fieldType == int.class) {
            return (T) Integer.valueOf(ifBooleanToNumber(value).toString());
        }
        if (fieldType == Long.class || fieldType == long.class) {
            return (T) Long.valueOf(ifBooleanToNumber(value).toString());
        }
        if (fieldType == Boolean.class || fieldType == boolean.class) {
            if (value instanceof Number) {
                Number number = (Number) value;
                Boolean b = number.intValue() == 1;
                return (T) (b);
            }
            return (T) Boolean.valueOf(value.toString());
        }
        if (fieldType == Double.class || fieldType == double.class) {
            return (T) Double.valueOf(ifBooleanToNumber(value).toString());
        }
        if (fieldType == Float.class || fieldType == float.class) {
            return (T) Float.valueOf(ifBooleanToNumber(value).toString());
        }
        if (fieldType.isEnum()) {
            //枚举对象
            Object[] enumConstants = fieldType.getEnumConstants();
            for (Object enumObj : enumConstants) {
                //应该精确比较，要不然查询时大小写问题会带来困扰
                if (enumObj.toString().equals(value.toString())) {
                    return (T) enumObj;
                }
            }
        }
        // 如果没有找到适合的转换器，抛出异常或者返回默认值
        throw new IllegalArgumentException("Cannot convert value of type " + valueType
                + " to field type " + fieldType + ", Value is [" + value + "]");
    }

    public static void putAttributeConverterModel(Class<?> fieldType, AttributeConverterModel attributeConverter) {
        GENERAL_ATTRIBUTE_CONVERTER_MODEL.put(fieldType, attributeConverter);
    }

    public static Object ifBooleanToNumber(Object value) {
        if (value instanceof Boolean) {
            // true -> 1, false -> 0
            return Boolean.compare((Boolean) value, false);
        }
        return value;
    }
}
