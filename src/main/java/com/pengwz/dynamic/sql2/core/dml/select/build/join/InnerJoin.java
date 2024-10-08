package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

public class InnerJoin extends JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;
    private Consumer<Condition> onCondition;

    public InnerJoin(Class<?> tableClass,String alias, Consumer<Condition> onCondition) {
        super(alias);
        this.tableClass = tableClass;
        this.onCondition = onCondition;
    }

    public InnerJoin(CteTable cteTable, Consumer<Condition> onCondition) {
        super(null);
        this.cteTable = cteTable;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.INNER;
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    public CteTable getCteTable() {
        return cteTable;
    }

    public Consumer<Condition> getOnCondition() {
        return onCondition;
    }
}
