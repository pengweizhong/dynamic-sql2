package com.pengwz.dynamic.sql2.context.config;

import com.pengwz.dynamic.sql2.enums.SqlDialect;
import com.pengwz.dynamic.sql2.plugins.schema.DbSchemaMatcher;
import com.pengwz.dynamic.sql2.plugins.schema.impl.MysqlSchemaMatcher;
import com.pengwz.dynamic.sql2.plugins.schema.impl.OracleSchemaMatcher;

import java.util.LinkedHashSet;
import java.util.Set;

public class SqlContextProperties {
    // 扫描数据库包路径
    private String[] scanDatabasePackage;
    // 扫描表实体包路径
    private String[] scanTablePackage;
    // 以指定的数据库方言启动，默认为当前数据库方言
    private SqlDialect sqlDialect;
    // 是否开启兼容版本模式，开启后动态SQL以最终结果为目的尝试解决版本不兼容的问题
    // 该方案主要是为了解决因版本降低或迁移数据库的场景，尽可能降低迁移成本
    private boolean enableCompatibilityMode;
    // 否在查询中使用数据库模式（Schema）
    private boolean useSchemaInQuery;
    // 数据库命名空间匹配器
    private Set<DbSchemaMatcher> schemaMatchers;
    // 数据库版本
    private String databaseProductVersion;

    public static SqlContextProperties defaultSqlContextProperties() {
        SqlContextProperties sqlContextProperties = new SqlContextProperties();
        sqlContextProperties.enableCompatibilityMode = false;
        sqlContextProperties.useSchemaInQuery = false;
        Set<DbSchemaMatcher> dbSchemaMatchers = new LinkedHashSet<>();
        dbSchemaMatchers.add(new MysqlSchemaMatcher());
        dbSchemaMatchers.add(new OracleSchemaMatcher());
        sqlContextProperties.schemaMatchers = dbSchemaMatchers;
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

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public boolean isEnableCompatibilityMode() {
        return enableCompatibilityMode;
    }

    public void setEnableCompatibilityMode(boolean enableCompatibilityMode) {
        this.enableCompatibilityMode = enableCompatibilityMode;
    }

    public Set<DbSchemaMatcher> getSchemaMatchers() {
        return schemaMatchers;
    }

    public void setSchemaMatchers(Set<DbSchemaMatcher> schemaMatchers) {
        this.schemaMatchers = schemaMatchers;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public void setDatabaseProductVersion(String databaseProductVersion) {
        this.databaseProductVersion = databaseProductVersion;
    }

    public boolean isUseSchemaInQuery() {
        return useSchemaInQuery;
    }

    public void setUseSchemaInQuery(boolean useSchemaInQuery) {
        this.useSchemaInQuery = useSchemaInQuery;
    }
}