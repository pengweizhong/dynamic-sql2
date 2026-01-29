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

/**
 * 表示一个可渲染为 SQL 字符串的元素。
 * <p>
 * 实现该接口的对象负责根据 {@link RenderContext} 提供的上下文信息，
 * 生成最终可执行的 SQL 片段。例如：
 * <ul>
 *     <li>函数调用表达式</li>
 *     <li>列引用</li>
 *     <li>表引用</li>
 *     <li>复杂的 SQL 片段（CASE WHEN、子查询等）</li>
 * </ul>
 * <p>
 * 渲染逻辑应保持无副作用，且不应修改传入的上下文对象。
 */
public interface SqlRenderable {

    /**
     * 根据给定的渲染上下文生成 SQL 字符串。
     *
     * @param context 渲染上下文，包含方言、别名、版本等信息
     * @return 渲染后的 SQL 字符串
     */
    String render(RenderContext context);
}
