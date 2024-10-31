package com.pengwz.dynamic.sql2.core.placeholder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

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

    public Collection<Object> getValues() {
        return parameters.values();
    }

}
