package com.pengwz.dynamic.sql2.table;

import java.util.List;

public class TableMeta {
//    //映射实体类
//    private Class<?> tableClass;
    //表名称
    private String tableName;
    //表别名
    private String tableAlias;
    //绑定到数据源名称
    private String bindDataSourceName;
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

    public String getTableAlias() {
        return tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }

//    public Class<?> getTableClass() {
//        return tableClass;
//    }
//
//    public void setTableClass(Class<?> tableClass) {
//        this.tableClass = tableClass;
//    }
}
