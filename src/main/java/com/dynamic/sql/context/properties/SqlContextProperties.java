package com.dynamic.sql.context.properties;


import com.dynamic.sql.interceptor.SqlInterceptor;
import com.dynamic.sql.plugins.schema.DbSchemaMatcher;
import com.dynamic.sql.plugins.schema.impl.MysqlSchemaMatcher;
import com.dynamic.sql.plugins.schema.impl.OracleSchemaMatcher;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class SqlContextProperties {
    // 扫描数据库包路径
    private String[] scanDatabasePackage;
    // 扫描表实体包路径
    private String[] scanTablePackage;
    // 扫描Mapper包路径
    private String[] scanMapperPackage;
    // 数据库命名空间匹配器
    private Set<DbSchemaMatcher> schemaMatchers = new LinkedHashSet<>();
    // 指定具体的命名空间配置
    private List<SchemaProperties> schemaProperties = new ArrayList<>();
    // 加载自定义拦截器
    private List<SqlInterceptor> interceptors = new ArrayList<>();

    public static SqlContextProperties defaultSqlContextProperties() {
        SqlContextProperties sqlContextProperties = new SqlContextProperties();
        Set<DbSchemaMatcher> dbSchemaMatchers = new LinkedHashSet<>();
        dbSchemaMatchers.add(new MysqlSchemaMatcher());
        dbSchemaMatchers.add(new OracleSchemaMatcher());
        sqlContextProperties.setSchemaMatchers(dbSchemaMatchers);
        return sqlContextProperties;
    }

    public String[] getScanDatabasePackage() {
        return scanDatabasePackage;
    }

    public void setScanDatabasePackage(String... scanDatabasePackage) {
        this.scanDatabasePackage = scanDatabasePackage;
    }

    public String[] getScanTablePackage() {
        return scanTablePackage;
    }

    public void setScanTablePackage(String... scanTablePackage) {
        this.scanTablePackage = scanTablePackage;
    }

    public String[] getScanMapperPackage() {
        return scanMapperPackage;
    }

    public void setScanMapperPackage(String... scanMapperPackage) {
        this.scanMapperPackage = scanMapperPackage;
    }

    public Set<DbSchemaMatcher> getSchemaMatchers() {
        return schemaMatchers;
    }

    public void setSchemaMatchers(Set<DbSchemaMatcher> schemaMatchers) {
        this.schemaMatchers = schemaMatchers;
    }

    public List<SchemaProperties> getSchemaProperties() {
        return schemaProperties;
    }

    public void addSchemaProperties(SchemaProperties schemaProperties) {
        this.schemaProperties.add(schemaProperties);
    }

    public List<SqlInterceptor> getInterceptors() {
        return interceptors;
    }

    public void addInterceptor(SqlInterceptor interceptor) {
        if (interceptors.contains(interceptor)) {
            return;
        }
        interceptors.add(interceptor);
    }
}