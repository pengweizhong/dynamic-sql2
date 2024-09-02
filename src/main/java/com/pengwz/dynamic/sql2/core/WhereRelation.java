package com.pengwz.dynamic.sql2.core;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class WhereRelation {
    private final List<Relation> relations = new ArrayList<>();

    public WhereRelation(Supplier<Boolean> condition) {
        relations.add(new Relation(condition));
    }

    public <T> T result() {
        return null;
    }

    static class Relation {
        private Supplier<Boolean> condition;

        public Relation(Supplier<Boolean> condition) {
            this.condition = condition;
        }
    }
}
