package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

public class CteJoin {
    private JoinTableType joinTableType;
    private Class<?> tableClass;
    private CteTable cteTable;
    private Consumer<Condition> onCondition;

    public CteJoin(JoinTableType joinTableType, Class<?> tableClass, Consumer<Condition> onCondition) {
        this.tableClass = tableClass;
        this.joinTableType = joinTableType;
        this.onCondition = onCondition;
    }

    public CteJoin(JoinTableType joinTableType, CteTable cteTable, Consumer<Condition> onCondition) {
        this.cteTable = cteTable;
        this.joinTableType = joinTableType;
        this.onCondition = onCondition;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    public Consumer<Condition> getOnCondition() {
        return onCondition;
    }

    public JoinTableType getJoinTableType() {
        return joinTableType;
    }

    public CteTable getCteTable() {
        return cteTable;
    }
}
