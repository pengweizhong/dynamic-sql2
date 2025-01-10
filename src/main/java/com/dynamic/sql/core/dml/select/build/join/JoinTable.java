package com.dynamic.sql.core.dml.select.build.join;


import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;
import java.util.function.Supplier;

public abstract class JoinTable {
    private String tableAlias;

    protected JoinTable(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public abstract JoinTableType getJoinTableType();

    public abstract Class<?> getTableClass();

    public abstract CteTable getCteTable();

    public abstract Consumer<Condition> getOnCondition();

    public String getTableAlias() {
        return tableAlias;
    }

    public Supplier<TableFunction> getTableFunction() {
        return null;
    }
}
