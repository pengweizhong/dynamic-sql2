package com.pengwz.dynamic.sql2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WhereCondition {
    private final List<Condition> conditions = new ArrayList<>();

    public WhereCondition() {

    }

    public <T> T result() {
        return null;
    }

    public <R> List<R> toList() {
        return null;
    }

    static class Condition {
        private Supplier<Boolean> condition;

        public Condition(Supplier<Boolean> condition) {
            this.condition = condition;
        }
    }
}
