package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;

public class SqlContextHelper {
    private SqlContextHelper() {
    }

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
}
