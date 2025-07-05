/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.interceptor;

/**
 * 控制 SQL 执行的枚举类型。
 * 用于表示拦截器在 SQL 执行链中的决策。
 */
public enum ExecutionControl {
    /**
     * 继续执行 SQL。
     * 表示拦截器允许 SQL 正常流转并执行。
     */
    PROCEED,
    /**
     * 跳过执行 SQL。
     * 表示拦截器决定不执行当前 SQL，通常用于短路逻辑或特殊处理。
     */
    SKIP,
    // /**
    //  * 中断执行。
    //  * 未来扩展值，用于表示完全停止执行并抛出异常或采取其他处理；目前好像没有必要？
    //  */
    // HALT
    ;
}
