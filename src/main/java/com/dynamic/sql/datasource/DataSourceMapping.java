package com.dynamic.sql.datasource;

import javax.sql.DataSource;

public class DataSourceMapping {
    private String dataSourceName;
    private DataSource dataSource;
    private boolean isGlobalDefault;
    private String[] bindBasePackages;
    //对于数据源名称重复的，是否允许覆盖？
    private boolean allowDataSourceDefinitionOverriding;

    public DataSourceMapping(String dataSourceName, DataSource dataSource, boolean isGlobalDefault, String[] bindBasePackages) {
        this.dataSourceName = dataSourceName;
        this.dataSource = dataSource;
        this.isGlobalDefault = isGlobalDefault;
        this.bindBasePackages = bindBasePackages;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
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

    public boolean isAllowDataSourceDefinitionOverriding() {
        return allowDataSourceDefinitionOverriding;
    }

    public void setAllowDataSourceDefinitionOverriding(boolean allowDataSourceDefinitionOverriding) {
        this.allowDataSourceDefinitionOverriding = allowDataSourceDefinitionOverriding;
    }
}
