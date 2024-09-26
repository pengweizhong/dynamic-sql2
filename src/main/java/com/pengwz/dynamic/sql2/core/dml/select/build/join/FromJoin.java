package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

public class FromJoin implements JoinTable {
    private Class<?> tableClass;
    private CteTable cteTable;

    public FromJoin(Class<?> tableClass) {
        this.tableClass = tableClass;
    }

    public FromJoin(CteTable cteTable) {
        this.cteTable = cteTable;
    }

    @Override
    public JoinTableType getJoinTableType() {
        throw new UnsupportedOperationException();
    }
}
