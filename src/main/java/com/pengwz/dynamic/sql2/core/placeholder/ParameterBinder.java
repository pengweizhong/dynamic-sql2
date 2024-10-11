package com.pengwz.dynamic.sql2.core.placeholder;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;
import com.pengwz.dynamic.sql2.utils.SqlUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParameterBinder {
    private final Map<String, Object> parameters = new HashMap<>();

    public String generateBindingKey() {
        //Key 的构成："^:[0-9a-f]{32}$"
        return ":" + UUID.randomUUID().toString().replace("-", "");
    }

    public String registerValueWithKey(Object value) {
        String key = generateBindingKey();
        parameters.put(key, value);
        return key;
    }

    public String registerValueWithKey(Fn<?, ?> fn, Object value) {
        String key = generateBindingKey();
        Object fixValue;
        //如果是枚举类直接实现了它？
        if (value instanceof AttributeConverter) {
            AttributeConverter attributeConverter = (AttributeConverter) value;
            fixValue = attributeConverter.convertToDatabaseColumn(value);
            parameters.put(key, fixValue);
            return key;
        }
        Fn originalFn = ReflectUtils.getOriginalFn(fn);
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(originalFn);
        String fieldName = ReflectUtils.fnToFieldName(originalFn);
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        ColumnMeta columnMeta = tableMeta.getColumnMeta(fieldName);
        if (columnMeta.getConverter() != null) {
            fixValue = ConverterUtils.loadCustomConverter(columnMeta.getConverter()).convertToDatabaseColumn(value);
        } else {
            fixValue = ConverterUtils.convertValueToDatabase(value);
        }
        parameters.put(key, fixValue);
        return key;
    }

    public void addParameterBinder(ParameterBinder parameterBinder) {
        if (parameterBinder == null) {
            return;
        }
        parameters.putAll(parameterBinder.getParameters());
    }

    protected Map<String, Object> getParameters() {
        return parameters;
    }

    public void add(String key, Object value) {
        parameters.put(key, value);
    }

    public boolean contains(String key) {
        return parameters.containsKey(key);
    }

    public Object getValue(String key) {
        return parameters.get(key);
    }

    public StringBuilder replacePlaceholdersWithValues(String sql) {
        StringBuilder modifiedSql = new StringBuilder(sql);
        Pattern uuidPattern = Pattern.compile(":[0-9a-f]{32}");
        Matcher matcher = uuidPattern.matcher(sql);
        while (matcher.find()) {
            String placeholder = matcher.group();
            if (contains(placeholder)) {
                Object value = SqlUtils.formattedParameter(getValue(placeholder));
                // 替换占位符为对应的值
                int start = matcher.start();
                int end = matcher.end();
                modifiedSql.replace(start, end, value.toString());
                matcher = uuidPattern.matcher(modifiedSql);
            }
        }
        return modifiedSql;
    }
}
