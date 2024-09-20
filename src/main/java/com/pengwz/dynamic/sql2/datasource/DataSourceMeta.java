package com.pengwz.dynamic.sql2.datasource;

import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.enums.SqlDialect;

import javax.sql.DataSource;

public class DataSourceMeta {
    //    //数据源名称
//    private String dataSourceName;
    //命名空间
    private String schema;
    //是否全局默认数据源
    private boolean isGlobalDefault;
    //绑定的实体类路径
    private String[] bindBasePackages;
    //数据源原始对象
    private DataSource dataSource;
    //数据源类型
    private DbType dbType;
    //数据库方言
    private SqlDialect sqlDialect;
    //数据源版本号
    private String version;

//    public String getDataSourceName() {
//        return dataSourceName;
//    }
//
//    public void setDataSourceName(String dataSourceName) {
//        this.dataSourceName = dataSourceName;
//    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
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

    public void setBindBasePackages(String[] bindBasePackages) {
        this.bindBasePackages = bindBasePackages;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public DbType getDbType() {
        return dbType;
    }

    public void setDbType(DbType dbType) {
        this.dbType = dbType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }
}
