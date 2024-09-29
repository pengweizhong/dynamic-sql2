package com.pengwz.dynamic.sql2.core.placeholder;

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
                Object value = getValue(placeholder);
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
