package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

public class SelfJoin implements JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;
    private String alias;
    private Consumer<Condition> onCondition;

    public SelfJoin(Class<?> tableClass, String alias, Consumer<Condition> onCondition) {
        this.tableClass = tableClass;
        this.alias = alias;
        this.onCondition = onCondition;
    }

    public SelfJoin(CteTable cteTable, String alias, Consumer<Condition> onCondition) {
        this.cteTable = cteTable;
        this.alias = alias;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.SELF;
    }
}
