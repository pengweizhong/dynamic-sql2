package com.dynamic.sql.context;


import com.dynamic.sql.context.properties.SchemaProperties;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SchemaContextHolder {
    //Key 是数据源名称
    private static final Map<String, SchemaProperties> SCHEMA_PROPERTIES_MAP = new ConcurrentHashMap<>();

    private SchemaContextHolder() {
    }

    protected static void addSchemaProperties(SchemaProperties schemaProperties) {
        SCHEMA_PROPERTIES_MAP.putIfAbsent(schemaProperties.getDataSourceName(), schemaProperties);
    }

    public static SchemaProperties getSchemaProperties(String dataSourceName) {
        return SCHEMA_PROPERTIES_MAP.get(dataSourceName);
    }

    protected static void clear() {
        SCHEMA_PROPERTIES_MAP.clear();
    }

}