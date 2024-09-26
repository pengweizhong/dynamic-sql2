package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

public class FullJoin implements JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;
    private Consumer<Condition> onCondition;

    public FullJoin(Class<?> tableClass, Consumer<Condition> onCondition) {
        this.tableClass = tableClass;
        this.onCondition = onCondition;
    }

    public FullJoin(CteTable cteTable, Consumer<Condition> onCondition) {
        this.cteTable = cteTable;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.LEFT;
    }
}