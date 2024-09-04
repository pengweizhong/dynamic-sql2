package com.pengwz.dynamic.sql2.core;

public class JoinTableRelation {

    public AliasTableRelation join(Class<?> tableClass) {
        return new AliasTableRelation(null);
    }

    public OnJoinTableRelation on(String alias) {
        return new OnJoinTableRelation();
    }
}
