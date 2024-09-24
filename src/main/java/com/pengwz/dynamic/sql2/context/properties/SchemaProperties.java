package com.pengwz.dynamic.sql2.context.properties;

import com.pengwz.dynamic.sql2.enums.SqlDialect;

public class SchemaProperties {
    // 数据源名称
    private String dataSourceName;
    // 以指定的数据库方言启动，默认为当前数据库方言
    private SqlDialect sqlDialect;
    // 是否开启兼容版本模式，开启后动态SQL以最终结果为目的尝试解决版本不兼容的问题
    // 该方案主要是为了解决因版本降低或迁移数据库的场景，尽可能降低迁移成本
    private boolean enableCompatibilityMode;
    // 否在查询中使用数据库模式（Schema）
    private boolean useSchemaInQuery;
    // 强制指定兼容的数据库版本
    private String databaseProductVersion;

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
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

    public boolean isUseSchemaInQuery() {
        return useSchemaInQuery;
    }

    public void setUseSchemaInQuery(boolean useSchemaInQuery) {
        this.useSchemaInQuery = useSchemaInQuery;
    }

    public String getDatabaseProductVersion() {
        return databaseProductVersion;
    }

    public void setDatabaseProductVersion(String databaseProductVersion) {
        this.databaseProductVersion = databaseProductVersion;
    }
}
