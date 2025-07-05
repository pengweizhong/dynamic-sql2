/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database;

import java.util.ArrayList;
import java.util.List;

/**
 * PreparedSql 类用于封装 SQL 语句及其对应的参数，以支持批量执行。
 * 该类包含 SQL 语句字符串和参数列表，适用于单次或批量的 SQL 执行场景。
 */
public class PreparedSql {
    /**
     * SQL 语句字符串
     */
    private String sql;

    /**
     * 参数列表的列表，用于支持批量参数传递。
     * 每个内部列表代表一组对应 SQL 语句的参数。
     */
    private List<List<Object>> params = new ArrayList<>();

    /**
     * 构造函数，初始化 SQL 语句和参数列表。
     *
     * @param sql    SQL 语句字符串
     * @param params 参数列表，包含单次执行所需的参数集合
     */
    public PreparedSql(String sql, List<Object> params) {
        this.sql = sql;
        this.params.add(params);
    }

    public PreparedSql(String sql) {
        this.sql = sql;
    }

    /**
     * 获取 SQL 语句。
     *
     * @return SQL 语句字符串
     */
    public String getSql() {
        return sql;
    }

    /**
     * 获取参数列表的第一个集合，适用于非批量操作场景。
     *
     * @return 单次执行所需的参数列表
     */
    public List<Object> getParams() {
        return params.get(0);
    }

    /**
     * 获取批量参数列表，适用于批量操作。
     * 每个内部列表对应一次 SQL 执行所需的参数集合。
     *
     * @return 包含批量参数的列表
     */
    public List<List<Object>> getBatchParams() {
        return params;
    }

    public void addBatchParams(List<Object> params) {
        this.params.add(params);
    }

    public boolean isBatch() {
        return params.size() > 1;
    }
}

