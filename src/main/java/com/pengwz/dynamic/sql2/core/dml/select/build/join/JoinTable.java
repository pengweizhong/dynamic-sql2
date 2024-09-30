package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;
import com.pengwz.dynamic.sql2.enums.JoinTableType;

public abstract class JoinTable {
    private String tableAlias;

    protected JoinTable(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    public abstract JoinTableType getJoinTableType();

    public abstract Class<?> getTableClass();

    public abstract CteTable getCteTable();

    public String getTableAlias() {
        return tableAlias;
    }

}
