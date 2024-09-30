package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

import java.util.function.Consumer;

public class CrossJoin extends JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;

    public CrossJoin(Class<?> tableClass) {
        super(null);
        this.tableClass = tableClass;
    }

    public CrossJoin(CteTable cteTable) {
        super(null);
        this.cteTable = cteTable;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.CROSS;
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
        return null;
    }

}
