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
import com.dynamic.sql.core.dml.select.SelectDsl;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;

public class NestedJoin extends JoinTable {
    private final SelectDsl nestedSelect;
    private SqlStatementSelectWrapper sqlStatementWrapper;
    private Consumer<GenericWhereCondition> onCondition;
    private JoinTableType joinTableType;

    public NestedJoin(SelectDsl nestedSelect, String tableAlias) {
        super(tableAlias);
        this.nestedSelect = nestedSelect;
    }

    public NestedJoin(JoinTableType joinTableType, SelectDsl nestedSelect,
                      String tableAlias, Consumer<GenericWhereCondition> onCondition) {
        super(tableAlias);
        this.joinTableType = joinTableType;
        this.nestedSelect = nestedSelect;
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

    public SelectDsl getNestedSelect() {
        return nestedSelect;
    }

    public SqlStatementSelectWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }

    public void setSqlStatementWrapper(SqlStatementSelectWrapper sqlStatementWrapper) {
        this.sqlStatementWrapper = sqlStatementWrapper;
    }
}
