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

import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.ColumnReference;
import com.dynamic.sql.core.dml.select.build.SelectSpecification;

public class Select {

    private final SelectSpecification selectSpecification = new SelectSpecification();

    public AbstractColumnReference loadColumReference() {
        return new ColumnReference(selectSpecification);
    }

    public Select() {
    }

    /**
     * 嵌套查询时调用此参数
     */
    public Select(NestedMeta nestedMeta) {
        selectSpecification.setNestedMeta(nestedMeta);
    }

    public SelectSpecification getSelectSpecification() {
        return selectSpecification;
    }

}
