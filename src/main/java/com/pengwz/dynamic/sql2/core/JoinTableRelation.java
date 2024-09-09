package com.pengwz.dynamic.sql2.core;

public class JoinTableRelation {

    public AliasTableRelation join(Class<?> tableClass) {
        return new AliasTableRelation(null);
    }

    public JoinTableRelation on(OnJoinTableCondition alias) {
        return new JoinTableRelation();
    }
}
