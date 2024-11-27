package com.pengwz.dynamic.sql2.plugins.conversion;

import java.util.Map;

public interface FetchResultConverter<R> extends ObjectValueConverter<Map<String, Object>, R> {
    @Override
    R convertValueTo(Map<String, Object> value);
}
