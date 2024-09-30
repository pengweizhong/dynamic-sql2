package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

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

}
