package com.dynamic.sql.model;

public class TableAliasMapping {
    private String alias;
    private boolean isNestedJoin;

    public TableAliasMapping() {
        this.alias = alias;
    }

    public TableAliasMapping(String alias, boolean isNestedJoin) {
        this.alias = alias;
        this.isNestedJoin = isNestedJoin;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isNestedJoin() {
        return isNestedJoin;
    }

    public void setIsNestedJoin(boolean nestedJoin) {
        isNestedJoin = nestedJoin;
    }

}
