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

import com.dynamic.sql.core.placeholder.ParameterBinder;


/**
 * 表示一个支持参数绑定（placeholder binding）的 SQL 片段。
 * <p>
 * 实现该接口的对象通常包含动态参数，需要在 SQL 执行阶段
 * 将实际的运行时值绑定到这些参数上。绑定逻辑由 {@link ParameterBinder} 负责。
 * <p>
 * 该接口仅描述“可绑定”这一能力，不涉及 SQL 渲染或其他行为。
 */
public interface Bindable {

    /**
     * 返回与当前 SQL 元素关联的参数绑定器。
     * <p>
     * 若当前元素不包含任何动态参数，则可返回 {@code null}。
     *
     * @return 参数绑定器，或 {@code null}（表示无需绑定）
     */
    ParameterBinder parameterBinder();
}
