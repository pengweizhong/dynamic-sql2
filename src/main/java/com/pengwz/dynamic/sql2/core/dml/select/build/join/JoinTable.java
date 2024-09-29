package com.pengwz.dynamic.sql2.core.dml.select.build.join;

import com.pengwz.dynamic.sql2.enums.JoinTableType;

public abstract class JoinTable {
    private String tableAlias;

    protected JoinTable(String tableAlias) {
        this.tableAlias = tableAlias;
    }

    abstract JoinTableType getJoinTableType();

    public String getTableAlias() {
        return tableAlias;
    }
}
