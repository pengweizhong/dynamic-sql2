package com.dynamic.sql.plugins.resolve;

import java.util.HashMap;

public class DefaultValueParser implements ValueParser {

    @Override
    public ValueResolver getValueResolver() {
        return new ValueResolver(new HashMap<>());
    }
}
