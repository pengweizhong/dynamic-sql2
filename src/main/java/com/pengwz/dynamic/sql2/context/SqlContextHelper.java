package com.pengwz.dynamic.sql2.context;

import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.context.properties.SqlContextProperties;
import com.pengwz.dynamic.sql2.core.SqlContext;
import com.pengwz.dynamic.sql2.interceptor.SqlInterceptorChain;
import com.pengwz.dynamic.sql2.mapper.MapperProxyFactory;

public class SqlContextHelper {
    private SqlContextHelper() {
    }

    public static SqlContext createSqlContext(SqlContextProperties sqlContextProperties) {
        SqlContextConfigurer sqlContextConfigurer = new SqlContextConfigurer(sqlContextProperties, new DefaultSqlContext());
        sqlContextConfigurer.initializeContext();
        for (SchemaProperties schemaProperty : sqlContextConfigurer.getSqlContextProperties().getSchemaProperties()) {
            SchemaContextHolder.addSchemaProperties(schemaProperty);
        }
        sqlContextProperties.getInterceptors().forEach(SqlInterceptorChain.getInstance()::addInterceptor);
        if (sqlContextProperties.getScanMapperPackage() != null && sqlContextProperties.getScanMapperPackage().length != 0) {
            MapperProxyFactory.setSqlContext(sqlContextConfigurer.getSqlContext());
        }
        return sqlContextConfigurer.getSqlContext();
    }
}
