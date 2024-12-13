package com.dynamic.sql.plugins.conversion;


import java.util.Map;

public interface FetchResultConverter<R> extends ObjectValueConverter<Map<String, Object>, R> {
    @Override
    R convertValueTo(Map<String, Object> value);
}
