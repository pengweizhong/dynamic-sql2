package com.pengwz.dynamic.sql2;

import com.pengwz.dynamic.sql2.enums.DbType;

public class SqlContextProperties {
    // 扫描表实体包路径
    private String[] scanTablePackage;
    // 扫描数据库包路径
    private String[] scanDatabasePackage;
    // 是否开启候补实时加载表，若表未扫描到则实时加载，否则抛出异常
    private boolean enableRealTimeLoad = false;
    // 以指定的数据库模式启动，默认为当前数据库类型
    private DbType databaseMode;
    // 是否开启兼容版本模式，开启后动态SQL以最终结果为目的尝试解决版本不兼容的问题
    // 该方案主要是为了解决因版本降低或迁移数据库的场景
    private boolean enableCompatibilityMode;

    public String[] getScanTablePackage() {
        return scanTablePackage;
    }

    public void setScanTablePackage(String... scanTablePackage) {
        this.scanTablePackage = scanTablePackage;
    }

    public String[] getScanDatabasePackage() {
        return scanDatabasePackage;
    }

    public void setScanDatabasePackage(String... scanDatabasePackage) {
        this.scanDatabasePackage = scanDatabasePackage;
    }

    public boolean isEnableRealTimeLoad() {
        return enableRealTimeLoad;
    }

    public void setEnableRealTimeLoad(boolean enableRealTimeLoad) {
        this.enableRealTimeLoad = enableRealTimeLoad;
    }

    public DbType getDatabaseMode() {
        return databaseMode;
    }

    public void setDatabaseMode(DbType databaseMode) {
        this.databaseMode = databaseMode;
    }

    public boolean isEnableCompatibilityMode() {
        return enableCompatibilityMode;
    }

    public void setEnableCompatibilityMode(boolean enableCompatibilityMode) {
        this.enableCompatibilityMode = enableCompatibilityMode;
    }
}