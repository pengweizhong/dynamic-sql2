/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build.join;


import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;

public class FullJoin extends JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;
    private Consumer<GenericWhereCondition> onCondition;

    public FullJoin(Class<?> tableClass, String alias, Consumer<GenericWhereCondition> onCondition) {
        super(alias);
        this.tableClass = tableClass;
        this.onCondition = onCondition;
    }

    public FullJoin(CteTable cteTable, Consumer<GenericWhereCondition> onCondition) {
        super(null);
        this.cteTable = cteTable;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.FULL;
    }

    @Override
    public Class<?> getTableClass() {
        return tableClass;
    }

    @Override
    public CteTable getCteTable() {
        return cteTable;
    }

    @Override
    public Consumer<GenericWhereCondition> getOnCondition() {
        return onCondition;
    }
}
