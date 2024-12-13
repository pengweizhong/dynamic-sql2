package com.dynamic.sql.plugins.schema.impl;


import com.dynamic.sql.enums.DbType;
import com.dynamic.sql.plugins.schema.DbSchemaMatcher;

public class OracleSchemaMatcher implements DbSchemaMatcher {
    @Override
    public String matchSchema(String url) {
        // 处理 Oracle 的 URL
        // Oracle SID URL 格式: jdbc:oracle:thin:@host:port:sid
        // Oracle 服务名称 URL 格式: jdbc:oracle:thin:@//host:port/service_name
        // 去掉jdbc:oracle:thin:@ 前缀
        String oracleUrl = url.substring("jdbc:oracle:thin:@".length());
        // 判断 URL 是否包含斜杠
        if (oracleUrl.startsWith("//")) {
            // 服务名称格式: jdbc:oracle:thin:@//host:port/service_name
            String[] parts = oracleUrl.substring(2).split("/");
            if (parts.length > 1) {
                return parts[1].split("\\?")[0];
            }
        } else {
            // SID 格式: jdbc:oracle:thin:@host:port:sid 或 jdbc:oracle:thin:@host:sid
            String[] parts = oracleUrl.split(":");
            if (parts.length == 3) {
                // host:port:sid 格式
                return parts[2];
            } else if (parts.length == 2) {
                // host:sid 格式
                return parts[1];
            }
        }
        return null;
    }

    @Override
    public boolean supports(DbType dbType) {
        return dbType.equals(DbType.ORACLE);
    }
}
