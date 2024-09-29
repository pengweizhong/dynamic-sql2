package com.pengwz.dynamic.sql2.enums;

public enum SortOrder {
    ASC, DESC;

    public String toSqlString(SqlDialect sqlDialect) {
        switch (sqlDialect) {
            case MYSQL:
            case MARIADB:
                return this.name().toLowerCase();
            default:
                return this.name();
        }
    }
}
