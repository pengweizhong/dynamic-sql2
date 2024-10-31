package com.pengwz.dynamic.sql2.core.placeholder;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper;
import com.pengwz.dynamic.sql2.table.ColumnMeta;
import com.pengwz.dynamic.sql2.table.TableMeta;
import com.pengwz.dynamic.sql2.table.TableProvider;
import com.pengwz.dynamic.sql2.utils.ConverterUtils;
import com.pengwz.dynamic.sql2.utils.ReflectUtils;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class ParameterBinder {
    private final Map<String, Object> parameters = new LinkedHashMap<>();

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
