package com.pengwz.dynamic.sql2.core.placeholder;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.plugins.conversion.AttributeConverter;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
        //如果是枚举类直接实现了它？
        if (value instanceof AttributeConverter) {
            AttributeConverter attributeConverter = (AttributeConverter) value;
            parameters.put(key, attributeConverter.convertToDatabaseColumn(value));
            return key;
        }
//        Fn originalFn = ReflectUtils.getOriginalFn(fn);
        if (fn instanceof AbstractAliasHelper) {
            parameters.put(key, ConverterUtils.convertValueToDatabase(value));
            return key;
        }
        String originalClassCanonicalName = ReflectUtils.getOriginalClassCanonicalName(fn);
        String fieldName = ReflectUtils.fnToFieldName(fn);
        TableMeta tableMeta = TableProvider.getTableMeta(originalClassCanonicalName);
        ColumnMeta columnMeta = tableMeta.getColumnMeta(fieldName);
        Object fixValue;
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

}
