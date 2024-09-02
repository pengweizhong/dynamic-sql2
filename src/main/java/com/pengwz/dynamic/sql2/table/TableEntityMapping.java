package com.pengwz.dynamic.sql2.table;

public class TableEntityMapping {
    private String tableName;
    private boolean isCache;
    private String bindDataSourceName;
    private Class<?> entityClass;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public String getBindDataSourceName() {
        return bindDataSourceName;
    }

    public void setBindDataSourceName(String bindDataSourceName) {
        this.bindDataSourceName = bindDataSourceName;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public void setEntityClass(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("{");
        sb.append("tableName='").append(tableName).append('\'');
        sb.append(", isCache=").append(isCache);
        sb.append(", bindDataSourceName='").append(bindDataSourceName).append('\'');
        sb.append(", entityClass=").append(entityClass.getCanonicalName());
        sb.append('}');
        return sb.toString();
    }
}
