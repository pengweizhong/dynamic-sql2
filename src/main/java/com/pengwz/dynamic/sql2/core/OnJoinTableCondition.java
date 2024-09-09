package com.pengwz.dynamic.sql2.core;

public class OnJoinTableCondition {
    private OnJoinTableCondition() {
    }

    public JoinTableRelation on() {
        return null;
    }


    public OnJoinTableCondition and(String alias) {
        return new OnJoinTableCondition();
    }

    public static OnJoinTableCondition alias(String alias) {
        OnJoinTableCondition onJoinTableCondition = new OnJoinTableCondition();

        return onJoinTableCondition;
    }
}
