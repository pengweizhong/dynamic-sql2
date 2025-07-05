/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.condition;


import com.dynamic.sql.core.dml.select.HavingCondition;

/**
 * WHERE 条件构造器抽象类，用于构建 SQL 查询中的 WHERE 子句。
 *
 * <p>支持嵌套条件（and/or）、函数条件（in、like、between 等），
 * 同时提供原始 SQL 条件拼接接口。</p>
 */
public abstract class WhereCondition<C extends WhereCondition<C>>
        implements NestedCondition<C>, FunctionCondition<C>, HavingCondition<C> {

    /**
     * 拼接自定义 SQL 条件。
     *
     * <p>该方法用于插入无法通过已有 API 构建的原始 SQL 条件，常用于处理复杂逻辑、数据库函数、自定义分区截取等情况。</p>
     *
     * <p><strong>注意：</strong> 该方法不做 SQL 安全校验，需确保传入字符串是安全且语法正确的 SQL 片段。</p>
     *
     * @param sql 自定义 SQL 条件表达式，例如 "and age > 18", "or row_num = 1", "exists (...)"
     * @return 返回当前条件构造器对象，用于链式调用
     */
    public abstract C sql(String sql);
}
