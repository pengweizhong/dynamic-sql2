package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

public class CrossJoin implements JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;

    public CrossJoin(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public CrossJoin(CteTable cteTable) {
        this.cteTable = cteTable;
    }

    @Override
    public JoinTableType getJoinTableType() {
        return JoinTableType.LEFT;
    }
}
