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
import com.dynamic.sql.enums.UnionType;

import java.util.function.Consumer;

public class UnionJoin extends JoinTable {
    private final SelectDsl[] nestedSelects;
    private SqlStatementSelectWrapper sqlStatementWrapper;
    private Consumer<GenericWhereCondition> onCondition;
    private JoinTableType joinTableType;
    private final UnionType unionType;

    public UnionJoin(SelectDsl[] nestedSelects, String tableAlias, UnionType unionType) {
        super(tableAlias);
        this.nestedSelects = nestedSelects;
        this.unionType = unionType;
    }

    public UnionJoin(JoinTableType joinTableType, SelectDsl[] nestedSelects,
                     String tableAlias, Consumer<GenericWhereCondition> onCondition, UnionType unionType) {
        super(tableAlias);
        this.joinTableType = joinTableType;
        this.nestedSelects = nestedSelects;
        this.onCondition = onCondition;
        this.unionType = unionType;
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

    public SelectDsl[] getNestedSelects() {
        return nestedSelects;
    }

    public SqlStatementSelectWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }

    public void setSqlStatementWrapper(SqlStatementSelectWrapper sqlStatementWrapper) {
        this.sqlStatementWrapper = sqlStatementWrapper;
    }

    public UnionType getUnionType() {
        return unionType;
    }

}
