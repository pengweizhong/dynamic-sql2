package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.utils.StringUtils;

import java.util.List;

public class TableMeta {
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

    public ColumnMeta getColumnMeta(String fieldName) {
        for (ColumnMeta columnMeta : columnMetas) {
            if (columnMeta.getField().getName().equals(fieldName)) {
                return columnMeta;
            }
        }
        return null;
    }

    public String getTableAlias() {
        return StringUtils.isEmpty(tableAlias) ? tableName : tableAlias;
    }

    public void setTableAlias(String tableAlias) {
        this.tableAlias = tableAlias;
    }
}