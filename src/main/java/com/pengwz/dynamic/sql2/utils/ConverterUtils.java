package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverterModel;
import com.pengwz.dynamic.sql2.plugins.conversion.impl.BigDecimalAttributeConverter;

import java.math.BigDecimal;
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
            return (T) Integer.valueOf(value.toString());
        }
        if (fieldType == Long.class || fieldType == long.class) {
            return (T) Long.valueOf(value.toString());
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
            return (T) Double.valueOf(value.toString());
        }
        if (fieldType == Float.class || fieldType == float.class) {
            return (T) Float.valueOf(value.toString());
        }
        if (fieldType.isEnum()) {
            //枚举对象
            Object[] enumConstants = fieldType.getEnumConstants();
            for (Object enumObj : enumConstants) {
                if (enumObj.toString().equalsIgnoreCase(valueType.toString().trim())) {
                    return (T) enumObj;
                }
            }
        }
        // 如果没有找到适合的转换器，抛出异常或者返回默认值
        throw new IllegalArgumentException("Cannot convert value of type " + valueType + " to field type " + fieldType);
    }

    public static void putAttributeConverterModel(Class<?> fieldType, AttributeConverterModel attributeConverter) {
        GENERAL_ATTRIBUTE_CONVERTER_MODEL.put(fieldType, attributeConverter);
    }
}
