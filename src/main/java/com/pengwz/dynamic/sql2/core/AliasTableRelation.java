package com.pengwz.dynamic.sql2.core;

public class AliasTableRelation {
    private TableRelation tableRelation;

    public AliasTableRelation(TableRelation tableRelation) {
        this.tableRelation = tableRelation;
    }

    public JoinTableRelation as(String alias) {
        return new JoinTableRelation();
    }


}
