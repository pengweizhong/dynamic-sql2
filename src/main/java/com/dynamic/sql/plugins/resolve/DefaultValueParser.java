package com.dynamic.sql.plugins.resolve;

import java.util.HashMap;
import java.util.Map;

public class DefaultValueParser implements ValueParser {
    private final Map<String, String> config;

    public DefaultValueParser() {
        this.config = new HashMap<>();
    }

    public DefaultValueParser(Map<String, String> config) {
        this.config = config;
    }

    @Override
    public ValueResolver getValueResolver() {
        return new ValueResolver(config);
    }
}
