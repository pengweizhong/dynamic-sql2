package com.dynamic.sql.core.dml.select.build.join;


import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;

public class SelfJoin extends JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;
    private Consumer<Condition> onCondition;

    public SelfJoin(Class<?> tableClass, String alias, Consumer<Condition> onCondition) {
        super(alias);
        this.tableClass = tableClass;
        this.onCondition = onCondition;
    }

    public SelfJoin(CteTable cteTable, Consumer<Condition> onCondition) {
        super(null);
        this.cteTable = cteTable;
        this.onCondition = onCondition;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.SELF;
    }

    @Override
    public Class<?> getTableClass() {
        return tableClass;
    }

    @Override
    public CteTable getCteTable() {
        return cteTable;
    }

    @Override
    public Consumer<Condition> getOnCondition() {
        return onCondition;
    }
}
