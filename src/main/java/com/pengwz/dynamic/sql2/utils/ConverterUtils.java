package com.pengwz.dynamic.sql2.utils;

import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.DefaultAttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.impl.LocalDateAttributeConverter;
import com.pengwz.dynamic.sql2.plugins.conversion.impl.NumberAttributeConverter;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;

public class ConverterUtils {
    //自定义转换器 搭配@Column注解使用
    private static final Map<Class<? extends AttributeConverter>, AttributeConverter> CUSTOM_ATTRIBUTE_CONVERTERS = new LinkedHashMap<>();
    //通用转换器,根据值本身的类型进行转换
    private static final Map<Class<?>, AttributeConverter> GENERAL_ATTRIBUTE_CONVERTERS = new LinkedHashMap<>();

    static {
        GENERAL_ATTRIBUTE_CONVERTERS.put(Number.class, new NumberAttributeConverter());
//        GENERAL_ATTRIBUTE_CONVERTERS.put(Enum.class, new EnumAttributeConverter());
        GENERAL_ATTRIBUTE_CONVERTERS.put(LocalDate.class, new LocalDateAttributeConverter());
    }

    private ConverterUtils() {
    }

    public static Object convertValueToDatabase(Object value) {
        if (value == null) {
            return null;
        }
        // 根据 value 的类型获取对应的转换器
        DefaultAttributeConverter<Object, Object> converter = matchConverter(value.getClass());
        return converter.convertToDatabaseColumn(value);
    }

    @SuppressWarnings("unchecked")
    private static <X, Y, T extends DefaultAttributeConverter<X, Y>> T matchConverter(Class<?> valueType) {
        //先判断是否被精准命中
        AttributeConverter attributeConverter = GENERAL_ATTRIBUTE_CONVERTERS.get(valueType);
        if (attributeConverter != null) {
            return (T) attributeConverter;
        }
        for (Map.Entry<Class<?>, AttributeConverter> converterEntry : GENERAL_ATTRIBUTE_CONVERTERS.entrySet()) {
            if (converterEntry.getKey().isAssignableFrom(valueType)) {
                return (T) converterEntry.getValue();
            }
        }
        throw new UnsupportedOperationException("Unable to match the appropriate type converter, " +
                "the error type occurred: " + valueType.getCanonicalName());
    }

    public static <Y> AttributeConverter<Object, Y> loadConverter(Class<? extends AttributeConverter> converterClass) {
        AttributeConverter attributeConverter = CUSTOM_ATTRIBUTE_CONVERTERS.get(converterClass);
        if (attributeConverter != null) {
            return attributeConverter;
        }
        AttributeConverter instance = ReflectUtils.instance(converterClass);
        CUSTOM_ATTRIBUTE_CONVERTERS.put(converterClass, instance);
        return instance;
    }

    public static void putCustomAttributeConverter(Class<? extends AttributeConverter> converterClass,
                                                   AttributeConverter attributeConverter) {
        CUSTOM_ATTRIBUTE_CONVERTERS.put(converterClass, attributeConverter);
    }

    public static void putGeneralAttributeConverter(Class<?> valueType, AttributeConverter attributeConverter) {
        GENERAL_ATTRIBUTE_CONVERTERS.put(valueType, attributeConverter);
    }

    public static void removeCustomAttributeConverter(Class<? extends AttributeConverter> converterClass) {
        CUSTOM_ATTRIBUTE_CONVERTERS.remove(converterClass);
    }

    public static void removeGeneralAttributeConverter(Class<?> valueType) {
        GENERAL_ATTRIBUTE_CONVERTERS.remove(valueType);
    }
}
