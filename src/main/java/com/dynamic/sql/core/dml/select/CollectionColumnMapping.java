/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.dml.select.build.column.ColumnQuery;

import java.util.ArrayList;
import java.util.List;

public class CollectionColumnMapping {
    private String targetProperty;
    private FieldFn<?, ?> parentKey;
    private FieldFn<?, ?> childKey;
    private List<ColumnQuery> childColumns = new ArrayList<>();

    public CollectionColumnMapping(FieldFn<?, ?> parentKey, FieldFn<?, ?> childKey) {
        this.parentKey = parentKey;
        this.childKey = childKey;
    }

    public String getTargetProperty() {
        return targetProperty;
    }

    public void setTargetProperty(String targetProperty) {
        this.targetProperty = targetProperty;
    }

    public FieldFn<?, ?> getParentKey() {
        return parentKey;
    }

    public FieldFn<?, ?> getChildKey() {
        return childKey;
    }

    public List<ColumnQuery> getChildColumns() {
        return childColumns;
    }

    public void addAllChildColumn(ColumnQuery childColumn) {
        this.childColumns.add(childColumn);
    }

    public void addAllChildColumns(List<ColumnQuery> childColumns) {
        this.childColumns.addAll(childColumns);
    }
}
