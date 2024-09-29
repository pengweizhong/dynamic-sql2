package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

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

}
