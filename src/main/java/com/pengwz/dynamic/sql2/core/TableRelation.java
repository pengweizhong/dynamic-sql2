package com.pengwz.dynamic.sql2.core;

import java.util.List;
import java.util.function.Supplier;

/**
 * 表关联关系
 */
public class TableRelation {
    private WhereRelation whereRelation;

    public TableRelation(String canonicalName) {

    }

    public WhereRelation where(Supplier<Boolean> condition) {
        if (condition == null) {
            throw new NullPointerException("condition is null");
        }
        if (whereRelation != null) {
            throw new UnsupportedOperationException("Only one 'where' can be declared for the first time.");
        }
        whereRelation = new WhereRelation(condition);
        return whereRelation;
    }

    public <R> List<R> toList() {
        return null;
    }

    public OnJoinTableRelation join(Class<?> tableClass) {
        return null;
    }

    static class Relation {
        String canonicalName;

        public Relation(String canonicalName) {
            this.canonicalName = canonicalName;
        }

        public String getCanonicalName() {
            return canonicalName;
        }

    }
}
