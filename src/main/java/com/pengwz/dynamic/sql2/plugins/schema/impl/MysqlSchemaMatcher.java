package com.pengwz.dynamic.sql2.plugins.schema.impl;

import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.plugins.schema.DbSchemaMatcher;

public class MysqlSchemaMatcher implements DbSchemaMatcher {
    @Override
    public String matchSchema(String url) {
        // 处理 MySQL 和 MariaDB 的 URL
        String[] parts = url.split("/");
        if (parts.length > 3) {
            // 取 URL 中的最后一部分作为 schema 名称
            return parts[3].split("\\?")[0];
        }
        return null;
    }

    @Override
    public boolean supports(DbType dbType) {
        return dbType.equals(DbType.MYSQL) || dbType.equals(DbType.MARIADB);
    }
}
