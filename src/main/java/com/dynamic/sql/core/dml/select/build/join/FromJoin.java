package com.dynamic.sql.core.dml.select.build.join;


import com.dynamic.sql.core.condition.Condition;
import com.dynamic.sql.core.dml.select.cte.CteTable;
import com.dynamic.sql.enums.JoinTableType;

import java.util.function.Consumer;

public class FromJoin extends JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;

    public FromJoin(Class<?> tableClass, String tableAlias) {
        super(tableAlias);
        this.tableClass = tableClass;
    }

    public FromJoin(CteTable cteTable) {
        super(null);
        this.cteTable = cteTable;
    }

    @Override
    public JoinTableType getJoinTableType() {
        throw new UnsupportedOperationException();
    }

    public Class<?> getTableClass() {
        return tableClass;
    }

    public CteTable getCteTable() {
        return cteTable;
    }

    @Override
    public Consumer<Condition> getOnCondition() {
        return null;
    }
}
