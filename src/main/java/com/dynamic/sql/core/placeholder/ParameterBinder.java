package com.dynamic.sql.core.placeholder;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

public class ParameterBinder {
    private final Map<String, Object> parameters = new LinkedHashMap<>();

    public ParameterBinder addParameterBinder(ParameterBinder parameterBinder) {
        if (parameterBinder == null) {
            return this;
        }
        parameters.putAll(parameterBinder.getParameters());
        return this;
    }

    public Map<String, Object> getParameters() {
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

    public Set<String> getKeys() {
        return parameters.keySet();
    }

    public Collection<Object> getValues() {
        return parameters.values();
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }
}