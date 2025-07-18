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

public class TableFunctionJoin extends JoinTable {
    private final Supplier<TableFunction> tableFunction;
    private Consumer<GenericWhereCondition> onCondition;
    private JoinTableType joinTableType;


    public TableFunctionJoin(JoinTableType joinTableType, Supplier<TableFunction> tableFunction,
                             String tableAlias, Consumer<GenericWhereCondition> onCondition) {
        super(tableAlias);
        this.joinTableType = joinTableType;
        this.tableFunction = tableFunction;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return joinTableType;
    }

    @Override
    public Class<?> getTableClass() {
        throw new UnsupportedOperationException();
    }

    @Override
    public CteTable getCteTable() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Consumer<GenericWhereCondition> getOnCondition() {
        return onCondition;
    }

    public Supplier<TableFunction> getTableFunction() {
        return tableFunction;
    }

}
