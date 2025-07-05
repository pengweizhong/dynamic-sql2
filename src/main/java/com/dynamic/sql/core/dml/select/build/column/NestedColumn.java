/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build.column;


import com.dynamic.sql.core.AbstractColumnReference;

import java.util.function.Consumer;

public class NestedColumn implements ColumnQuery {
    //别名
    private String alias;
    //嵌套列
    private Consumer<AbstractColumnReference> nestedColumnReference;

    //    public NestedColumn(Consumer<NestedSelect> nestedSelect, String alias) {
//        this.nestedSelect = nestedSelect;
//        this.alias = alias;
//    }
    public NestedColumn(Consumer<AbstractColumnReference> columnReferenceConsumer, String alias) {
        this.nestedColumnReference = columnReferenceConsumer;
        this.alias = alias;
    }

    @Override
    public String getAlias() {
        return alias;
    }

    public Consumer<AbstractColumnReference> getNestedColumnReference() {
        return nestedColumnReference;
    }

}
