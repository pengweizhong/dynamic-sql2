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

public class CustomOrderBy extends OrderBy {
    private final String orderingFragment;

    public CustomOrderBy(String orderingFragment) {
        super(null);
        this.orderingFragment = orderingFragment;
    }

    public String getOrderingFragment() {
        return orderingFragment;
    }

    @Override
    public String getTableAlias() {
        return "";
    }

    @Override
    public FieldFn getFieldFn() {
        return null;
    }

    @Override
    public String getColumnName() {
        return "";
    }
}
