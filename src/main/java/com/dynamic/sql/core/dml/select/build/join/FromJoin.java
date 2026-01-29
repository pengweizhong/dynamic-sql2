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


import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class FromJoin extends JoinTable {
    protected Class<?> tableClass;
    private CteTable cteTable;
    private Supplier<TableFunction> tableFunction;

    public FromJoin(Class<?> tableClass, String tableAlias) {
        super(tableAlias);
        this.tableClass = tableClass;
    }

    public FromJoin(Supplier<TableFunction> tableFunction, String tableAlias) {
        super(tableAlias);
        this.tableFunction = tableFunction;
    }

    public FromJoin(CteTable cteTable) {
        super(null);
        this.cteTable = cteTable;
    }

    @Override
    public JoinTableType getJoinTableType() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    public CteTable getCteTable() {
        return cteTable;
    }

    @Override
    public Consumer<GenericWhereCondition> getOnCondition() {
        return null;
    }

    @Override
    public Supplier<TableFunction> getTableFunction() {
        return tableFunction;
    }
}
