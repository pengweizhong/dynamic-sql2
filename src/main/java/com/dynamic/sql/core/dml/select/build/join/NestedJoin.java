package com.dynamic.sql.core.dml.select.build.join;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.dml.select.build.SqlStatementSelectWrapper;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;

public class NestedJoin extends JoinTable {
    private final Consumer<AbstractColumnReference> nestedSelect;
    private SqlStatementSelectWrapper sqlStatementWrapper;
    private Consumer<Condition> onCondition;
    private JoinTableType joinTableType;

    public NestedJoin(Consumer<AbstractColumnReference> nestedSelect, String tableAlias) {
        super(tableAlias);
        this.nestedSelect = nestedSelect;
    }

    public NestedJoin(JoinTableType joinTableType, Consumer<AbstractColumnReference> nestedSelect,
                      String tableAlias, Consumer<Condition> onCondition) {
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
    public Consumer<Condition> getOnCondition() {
        return onCondition;
    }

    public Consumer<AbstractColumnReference> getNestedSelect() {
        return nestedSelect;
    }

    public SqlStatementSelectWrapper getSqlStatementWrapper() {
        return sqlStatementWrapper;
    }

    public void setSqlStatementWrapper(SqlStatementSelectWrapper sqlStatementWrapper) {
        this.sqlStatementWrapper = sqlStatementWrapper;
    }
}
