package com.dynamic.sql.core.ddl;

public class TableExistenceChecker {
    //操作对象
    private Class<?> entityClass;
    private String tableName;

    public TableExistenceChecker(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public TableExistenceChecker(String tableName) {
        this.tableName = tableName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public String getTableName() {
        return tableName;
    }

    public boolean existTable() {
        return false;
    }
}
