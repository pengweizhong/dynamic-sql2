/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.order;

import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.enums.SortOrder;

public abstract class OrderBy {
    private final SortOrder sortOrder;

    protected OrderBy(SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortOrder getSortOrder() {
        return sortOrder;
    }

    public abstract String getTableAlias();

    public abstract FieldFn getFieldFn();

    public abstract String getColumnName();
}
