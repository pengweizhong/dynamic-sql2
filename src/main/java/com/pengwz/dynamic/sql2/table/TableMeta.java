package com.pengwz.dynamic.sql2.table;

import java.util.List;

class TableMeta {
    //表名称
    private String tableName;
    //绑定到数据源名称
    private String bindDataSourceName;
    //映射实体
    private String canonicalClassName;
    //列成员
    private List<ColumnMeta> columnMetas;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getBindDataSourceName() {
        return bindDataSourceName;
    }

    public void setBindDataSourceName(String bindDataSourceName) {
        this.bindDataSourceName = bindDataSourceName;
    }

    public List<ColumnMeta> getColumnMetas() {
        return columnMetas;
    }

    public void setColumnMetas(List<ColumnMeta> columnMetas) {
        this.columnMetas = columnMetas;
    }

    public String getCanonicalClassName() {
        return canonicalClassName;
    }

    public void setCanonicalClassName(String canonicalClassName) {
        this.canonicalClassName = canonicalClassName;
    }

    @Override
    public String toString() {
        return "TableMeta{" +
                "tableName='" + tableName + '\'' +
                ", bindDataSourceName='" + bindDataSourceName + '\'' +
                ", columnMetas=" + columnMetas +
                '}';
    }
}
