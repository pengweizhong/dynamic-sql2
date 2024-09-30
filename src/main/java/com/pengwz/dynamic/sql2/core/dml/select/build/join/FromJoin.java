package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

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
