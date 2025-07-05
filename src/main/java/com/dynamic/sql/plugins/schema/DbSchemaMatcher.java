/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.schema;


import com.dynamic.sql.enums.DbType;

/**
 * 插件接口，用于不同数据库的实现类根据 JDBC URL 获取正确的 schema 名称。
 * 每个数据库类型的实现类需自行定义如何从 URL 中解析 schema。
 */
public interface DbSchemaMatcher {

    /**
     * 根据传入的 JDBC URL 解析并返回该 URL 所对应的 schema 名称。
     *
     * @param url 数据库连接的 JDBC URL 字符串
     * @return 解析出的 schema 名称
     */
    String matchSchema(String url);

    /**
     * 判断该实现类是否支持给定的 SQL 方言（数据库类型）。
     *
     * @param dbType 数据库类型
     * @return 如果该实现类支持此方言，则返回 true；否则返回 false
     */
    boolean supports(DbType dbType);
}