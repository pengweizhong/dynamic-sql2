package com.pengwz.dynamic.sql2.core.dml.select.build.join;

public class FromNestedJoin extends FromJoin {
    private final NestedJoin nestedJoin;

    public FromNestedJoin(NestedJoin nestedJoin) {
        super(null, nestedJoin.getTableAlias());
        this.nestedJoin = nestedJoin;
    }

    public NestedJoin getNestedJoin() {
        return nestedJoin;
    }
}
