package com.dynamic.sql.context;


import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.context.properties.SqlContextProperties;
import com.dynamic.sql.context.properties.SqlLogConfig;
import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.interceptor.SqlInterceptorChain;

import java.util.function.Supplier;

public class SqlContextHelper {
    private SqlContextHelper() {
    }

    private static final ThreadLocal<SqlLogConfig> SQL_LOG_CONFIG_THREAD_LOCAL = new ThreadLocal<>();

    public static SqlContext createSqlContext(SqlContextProperties sqlContextProperties) {
        SqlContextConfigurer sqlContextConfigurer = createSqlContextConfigurer(sqlContextProperties);
        sqlContextConfigurer.initializeContext();
        sqlContextProperties.getInterceptors().forEach(SqlInterceptorChain.getInstance()::addInterceptor);
        return sqlContextConfigurer.getSqlContext();
    }

    public static SqlContextConfigurer createSqlContextConfigurer(SqlContextProperties sqlContextProperties) {
        return new SqlContextConfigurer(sqlContextProperties, new DefaultSqlContext());
    }

    public static void addSchemaProperties(SqlContextProperties sqlContextProperties) {
        for (SchemaProperties schemaProperty : sqlContextProperties.getSchemaProperties()) {
            SchemaContextHolder.addSchemaProperties(schemaProperty);
        }
    }

    public static <T> T suppressSqlLog(Supplier<T> supplier) {
        try {
            SQL_LOG_CONFIG_THREAD_LOCAL.set(new SqlLogConfig().setSuppressSqlLog(true));
            return supplier.get();
        } finally {
            SQL_LOG_CONFIG_THREAD_LOCAL.remove();
        }

    }

    public static void suppressSqlLog(Runnable runnable) {
        try {
            SQL_LOG_CONFIG_THREAD_LOCAL.set(new SqlLogConfig().setSuppressSqlLog(true));
            runnable.run();
        } finally {
            SQL_LOG_CONFIG_THREAD_LOCAL.remove();
        }
    }

    public static <T> T forceSqlLog(Supplier<T> supplier) {
        return supplier.get();
    }

    public static void forceSqlLog(Runnable runnable) {
        runnable.run();
    }

    public static SqlLogConfig getSqlLogConfig() {
        return SQL_LOG_CONFIG_THREAD_LOCAL.get();
    }
}
