package com.pengwz.dynamic.sql2.core.placeholder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ParameterBinder {
    private final Map<String, Object> parameters = new HashMap<>();

    public String generateBindingKey() {
        // // UUID 正则表达式
        //        Pattern uuidPattern = Pattern.compile("^:[0-9a-f]{32}$");

        return ":" + UUID.randomUUID().toString().replace("-", "");
    }

    public String registerValueWithKey(Object value) {
        String key = generateBindingKey();
        parameters.put(key, value);
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
