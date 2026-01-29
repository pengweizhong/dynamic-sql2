/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function;

import com.dynamic.sql.core.Fn;

/**
 * 表级函数（Table-Valued Function，TVF）。
 * <p>
 * 表级函数可作为 FROM 子句中的“虚拟表”参与查询，例如：
 * <pre>
 *     SELECT * FROM my_function(arg1, arg2)
 * </pre>
 * <p>
 * 实现该接口的对象需要具备：
 * <ul>
 *     <li>可渲染为 SQL（继承 {@link SqlRenderable}）</li>
 *     <li>可绑定参数（继承 {@link Bindable}）</li>
 *     <li>提供函数返回的列定义（如 TVF 的返回 schema）</li>
 * </ul>
 * <p>
 * 该接口仅描述表函数的结构，不限制具体的 SQL 方言或函数语法。
 */
public interface TableFunction extends SqlRenderable, Bindable {

    /**
     * 返回表函数的返回列定义。
     * <p>
     * 对于 table-valued function，其返回值通常是一个“虚拟表”，
     * 该表至少包含一个列，因此需要提供对应的列表达式。
     *
     * @return 函数返回的列表达式（通常为虚拟表的主列或唯一列）
     */
    Fn<?, ?> originColumn();
}
