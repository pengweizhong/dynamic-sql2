package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;

public class SqlContextHolder {
    private static final ThreadLocal<SqlContextConfigurer> contextHolder = new ThreadLocal<>();

    private SqlContextHolder() {
    }

    protected static void setSqlContextConfigurer(SqlContextConfigurer sqlContextConfigurer) {
        contextHolder.set(sqlContextConfigurer);
    }

    public static SqlContext getSqlContext() {
        return contextHolder.get().getSqlContext();
    }

    public static SqlContextProperties getSqlContextProperties() {
        return contextHolder.get().getSqlContextProperties();
    }

    protected static void clear() {
        contextHolder.remove();
    }
}