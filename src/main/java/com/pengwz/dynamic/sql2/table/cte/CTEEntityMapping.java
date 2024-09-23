package com.pengwz.dynamic.sql2.table.cte;

public class CTEEntityMapping {
    private String cteName;
    private boolean isCache;
    private Class<?> cteClass;
    public String getCteName() {
        return cteName;
    }

    public void setCteName(String cteName) {
        this.cteName = cteName;
    }

    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean cache) {
        isCache = cache;
    }

    public Class<?> getCteClass() {
        return cteClass;
    }

    public void setCteClass(Class<?> cteClass) {
        this.cteClass = cteClass;
    }
}