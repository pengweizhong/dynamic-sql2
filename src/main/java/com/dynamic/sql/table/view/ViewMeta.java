/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.table.view;


import com.dynamic.sql.table.FieldMeta;

import java.util.List;

public class ViewMeta {
    //绑定到数据源名称
    @Deprecated
    private String bindDataSourceName;
    //列成员
    private List<ViewColumnMeta> viewColumnMetas;

    public <T extends FieldMeta> List<T> getViewColumnMetas() {
        return (List<T>) viewColumnMetas;
    }

    public void setViewColumnMetas(List<ViewColumnMeta> viewColumnMetas) {
        this.viewColumnMetas = viewColumnMetas;
    }

    public String getBindDataSourceName() {
        return bindDataSourceName;
    }

    public void setBindDataSourceName(String bindDataSourceName) {
        this.bindDataSourceName = bindDataSourceName;
    }

    public ViewColumnMeta getViewColumnMetaByFieldName(String fieldName) {
        for (ViewColumnMeta columnMeta : viewColumnMetas) {
            if (columnMeta.getField().getName().equals(fieldName)) {
                return columnMeta;
            }
        }
        return null;
    }

    public ViewColumnMeta getViewColumnMetaByColumnName(String columnName) {
        for (ViewColumnMeta columnMeta : viewColumnMetas) {
            if (columnMeta.getColumnName().equals(columnName)) {
                return columnMeta;
            }
        }
        return null;
    }
}
