/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.context.properties;


import com.dynamic.sql.core.Version;
import com.dynamic.sql.enums.DbType;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.SqlUtils;

public class SchemaProperties {
    // 数据源名称
    private String dataSourceName;
    // 以指定的数据库方言启动，默认为当前数据库方言
    private SqlDialect sqlDialect;
    // 默认绑定的实体类、视图路径
    private String[] bindBasePackages;
    // 是否开启兼容版本模式，开启后动态SQL以最终结果为目的尝试解决版本不兼容的问题
    // 该方案主要是为了解决因版本降低或迁移数据库的场景，尽可能降低迁移成本
    private boolean enableCompatibilityMode;
    // 是否默认数据源，默认数据源只能存在一个
    private boolean isGlobalDefault = false;
    // 否在查询中使用数据库模式（Schema）
    private boolean useSchemaInQuery = false;
    // 否在查询中使用as关键字连接别名
    private boolean useAsInQuery = true;
    // 强制指定兼容的数据库版本
    private String databaseProductVersion;
    // 打印SQL语句
    private PrintSqlProperties printSqlProperties = new PrintSqlProperties();
    //主版本号
    private int majorVersionNumber;
    //次版本号
    private int minorVersionNumber;
    //补丁号
    private int patchVersionNumber;

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

    public void setDatabaseProductVersion(DbType dbType, String databaseProductVersion) {
        this.databaseProductVersion = databaseProductVersion;
        Version version = SqlUtils.databaseProductVersion(dbType, databaseProductVersion);
        this.majorVersionNumber = version.getMajorVersion();
        this.minorVersionNumber = version.getMinorVersion();
        this.patchVersionNumber = version.getPatchVersion();
    }

    public boolean isUseAsInQuery() {
        return useAsInQuery;
    }

    public void setUseAsInQuery(boolean useAsInQuery) {
        this.useAsInQuery = useAsInQuery;
    }

    public PrintSqlProperties getPrintSqlProperties() {
        return printSqlProperties;
    }

    public void setPrintSqlProperties(PrintSqlProperties printSqlProperties) {
        this.printSqlProperties = printSqlProperties;
    }

    public int getMajorVersionNumber() {
        return majorVersionNumber;
    }

    public int getMinorVersionNumber() {
        return minorVersionNumber;
    }

    public int getPatchVersionNumber() {
        return patchVersionNumber;
    }

    public boolean isGlobalDefault() {
        return isGlobalDefault;
    }

    public void setGlobalDefault(boolean globalDefault) {
        isGlobalDefault = globalDefault;
    }

    public String[] getBindBasePackages() {
        return bindBasePackages;
    }

    public void setBindBasePackages(String... bindBasePackages) {
        this.bindBasePackages = bindBasePackages;
    }

    public static class PrintSqlProperties {
        // 打印SQL语句
        private boolean printSql = true;
        // 打印SQL时是否打印数据源名称
        private boolean printDataSourceName = true;

        public boolean isPrintSql() {
            return printSql;
        }

        public void setPrintSql(boolean printSql) {
            this.printSql = printSql;
        }

        public boolean isPrintDataSourceName() {
            return printDataSourceName;
        }

        public void setPrintDataSourceName(boolean printDataSourceName) {
            this.printDataSourceName = printDataSourceName;
        }
    }
}
