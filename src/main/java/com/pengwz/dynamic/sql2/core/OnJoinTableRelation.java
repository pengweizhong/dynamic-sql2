package com.pengwz.dynamic.sql2.core;

public class OnJoinTableRelation {

    public JoinTableRelation on() {
        return null;
    }


    public OnJoinTableRelation and(String alias) {
        return new OnJoinTableRelation();
    }
}
