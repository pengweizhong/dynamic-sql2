package com.dynamic.sql.enums;

public enum DMLType implements SqlExecuteType {
    INSERT, UPSERT, DELETE, UPDATE, SELECT;

    @Override
    public String getType() {
        return this.name();
    }
}
