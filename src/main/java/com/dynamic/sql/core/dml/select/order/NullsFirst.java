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

public class NullsFirst extends OrderBy {
    public NullsFirst() {
        super(null);
    }

    protected NullsFirst(SortOrder sortOrder) {
        super(sortOrder);
    }

    @Override
    public String getTableAlias() {
        throw new UnsupportedOperationException("NullsFirst does not support table alias");
    }

    @Override
    public FieldFn getFieldFn() {
        throw new UnsupportedOperationException("NullsFirst does not support field fn");
    }

    @Override
    public String getColumnName() {
        throw new UnsupportedOperationException("NullsFirst does not support column name");
    }
}
