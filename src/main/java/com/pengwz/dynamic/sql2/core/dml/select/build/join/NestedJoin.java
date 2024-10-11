package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.select.build.SqlStatementWrapper;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

public class NestedJoin extends JoinTable {
    private final Consumer<AbstractColumnReference> nestedSelect;
    private SqlStatementWrapper sqlStatementWrapper;

    public NestedJoin(Consumer<AbstractColumnReference> nestedSelect, String tableAlias) {
        super(tableAlias);
        this.nestedSelect = nestedSelect;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.NESTED;
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
    public Consumer<Condition> getOnCondition() {
        throw new UnsupportedOperationException();
    }

    public Consumer<AbstractColumnReference> getNestedSelect() {
        return nestedSelect;
    }

    public SqlStatementWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }

    public void setSqlStatementWrapper(SqlStatementWrapper sqlStatementWrapper) {
        this.sqlStatementWrapper = sqlStatementWrapper;
    }
}
