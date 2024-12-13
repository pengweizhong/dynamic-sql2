package com.dynamic.sql.utils;


import com.dynamic.sql.core.column.function.AnonymousFunction;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.plugins.conversion.AttributeConverter;
import com.dynamic.sql.plugins.conversion.AttributeConverterModel;
import com.dynamic.sql.plugins.conversion.FetchResultConverter;
import com.dynamic.sql.plugins.conversion.attribute.BigDecimalAttributeConverter;
import com.dynamic.sql.plugins.conversion.attribute.LocalDateAttributeConverter;
import com.dynamic.sql.table.FieldMeta;

import java.math.BigDecimal;
import java.sql.Clob;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterUtils {
    //自定义转换器 搭配@Column注解使用
    private static final Map<Class<? extends AttributeConverter>, AttributeConverter> CUSTOM_ATTRIBUTE_CONVERTERS = new LinkedHashMap<>();
    private static final Map<Class<?>, AttributeConverterModel> GENERAL_ATTRIBUTE_CONVERTER_MODEL = new LinkedHashMap<>();
    private static final Map<Class<?>, FetchResultConverter> FETCH_RESULT_CONVERTER_MAP = new LinkedHashMap<>();
    // 缓存已创建的 DateTimeFormatter 实例  Key 是格式化字符串，如 yyyy-MM-dd
    private static final Map<String, DateTimeFormatter> FORMATTER_CACHE = new ConcurrentHashMap<>();

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

    public static Object convertToDatabaseColumn(SqlDialect sqlDialect, FieldMeta fieldMeta, Object value) {
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
        //是否存在格式化
        String format = fieldMeta.getFormat();
        if (format != null) {
            String formattedValue = formatterDateValue(value, format);
            if (sqlDialect == SqlDialect.ORACLE) {
                ParameterBinder parameterBinder = new ParameterBinder();
//                formattedValue = SqlUtils.registerValueWithKey(parameterBinder, formattedValue);
//                format = SqlUtils.registerValueWithKey(parameterBinder, format);
                if (value instanceof LocalDate || value instanceof java.sql.Date) {
                    return new AnonymousFunction("TO_DATE('" + formattedValue + "', '" + format + "')", parameterBinder);
                } else {
                    return new AnonymousFunction("TO_TIMESTAMP('" + formattedValue + "', '" + format + "')", parameterBinder);
                }
            }
            return formattedValue;
        }
        // 如果没有找到适合的转换器，抛出异常或者返回默认值
        return value;
    }

    public static String formatterDateValue(Object dateValue, String pattern) {
        TemporalAccessor temporal;
        if (dateValue instanceof TemporalAccessor) {
            temporal = (TemporalAccessor) dateValue;
        } else if (dateValue instanceof java.sql.Date) {
            java.sql.Date date = (java.sql.Date) dateValue;
            temporal = date.toLocalDate();
        } else if (dateValue instanceof java.sql.Time) {
            java.sql.Time time = (java.sql.Time) dateValue;
            temporal = time.toLocalTime();
        } else if (dateValue instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) dateValue;
            temporal = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } else {
            throw new UnsupportedOperationException("Unsupported date type: " + dateValue.getClass());
        }
        // 检查缓存中是否已有该格式的 DateTimeFormatter
        DateTimeFormatter formatter = FORMATTER_CACHE.computeIfAbsent(pattern, DateTimeFormatter::ofPattern);
        return formatter.format(temporal);
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
        // 检查是否有自定义的转换器
        AttributeConverter<Object, V> customAttributeConverter = CUSTOM_ATTRIBUTE_CONVERTERS.get(fieldType);
        if (customAttributeConverter != null) {
            return (T) customAttributeConverter.convertToEntityAttribute(value);
        }
        // 检查是否有通用的转换器
        AttributeConverter<Object, V> attributeConverter = GENERAL_ATTRIBUTE_CONVERTER_MODEL.get(fieldType);
        if (attributeConverter != null) {
            return (T) attributeConverter.convertToEntityAttribute(value);
        }
        //处理一下特别常见的类型
        if (fieldType == String.class) {
            //大字段需要特殊处理
            if (value instanceof Clob) {
                Clob clob = (Clob) value;
                // 使用 getSubString 方法一次性读取内容
                try {
                    return (T) clob.getSubString(1, ((Long) clob.length()).intValue());
                } catch (Exception e) {
                    throw new IllegalStateException("Clob cannot be written to String", e);
                }
            }
            return (T) value.toString();
        }
        if (fieldType == Integer.class || fieldType == int.class) {
            if (value instanceof Double) {
                Integer intValue = ((Double) value).intValue();
                return (T) intValue;
            }
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
        if (fieldType == LocalDateTime.class) {
            if (value instanceof Timestamp) {
                Timestamp timestamp = (Timestamp) value;
                return (T) timestamp.toLocalDateTime();
            }
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

    public static void putCustomAttributeConverter(Class<? extends AttributeConverter> fieldType, AttributeConverter attributeConverter) {
        CUSTOM_ATTRIBUTE_CONVERTERS.put(fieldType, attributeConverter);
    }

    public static void putFetchResultConverter(Class<?> targetType, FetchResultConverter fetchResultConverter) {
        //多次调用应该覆盖 这样符合实际应用场景
        FETCH_RESULT_CONVERTER_MAP.put(targetType, fetchResultConverter);
    }

    public static Object ifBooleanToNumber(Object value) {
        if (value instanceof Boolean) {
            // true -> 1, false -> 0
            return Boolean.compare((Boolean) value, false);
        }
        return value;
    }

    public static <R> FetchResultConverter<R> getFetchResultConverter(Class<R> resultClass) {
        return FETCH_RESULT_CONVERTER_MAP.get(resultClass);
    }

    public static <R, X, Y> AttributeConverter<X, Y> getCustomAttributeConverter(Class<R> resultClass) {
        return CUSTOM_ATTRIBUTE_CONVERTERS.get(resultClass);
    }
}