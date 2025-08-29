/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.table;


import com.dynamic.sql.utils.StringUtils;

import java.util.List;

public class TableMeta {
    //命名空间 允许跨库
    private String schema;
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

    public <T extends FieldMeta> List<T> getColumnMetas() {
        return (List<T>) columnMetas;
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

    public ColumnMeta getColumnPrimaryKey() {
        for (ColumnMeta columnMeta : columnMetas) {
            if (columnMeta.isPrimary()) {
                return columnMeta;
            }
        }
        return null;
    }

    public String getSchema() {
        return schema;
    }

    public void setSchema(String schema) {
        this.schema = schema;
    }
}