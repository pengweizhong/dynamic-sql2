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


import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.dml.SqlStatementWrapper;

import java.sql.Connection;

/**
 * SQL 拦截器接口，用于在 SQL 执行的不同阶段提供拦截和处理功能。
 * <p>
 * 实现该接口的类可以在 SQL 执行的不同阶段（执行前、跳过执行时、执行后）嵌入自定义逻辑。
 * 常见用途包括：日志记录、权限校验、动态修改 SQL、性能统计、或错误处理等。
 * </p>
 */
public interface SqlInterceptor {

    /**
     * 在 SQL 执行之前的拦截逻辑。
     * <p>
     * 此方法允许开发者在 SQL 被真正执行之前介入，进行检查、修改或控制。
     * 通过返回值控制后续行为，包括继续执行、跳过执行或终止执行。
     * </p>
     *
     * @param sqlStatementWrapper 包含原始 SQL、解析后的参数及上下文信息的包装对象，
     *                            可用作 SQL 检查或动态修改的依据。
     * @param connection          数据库连接，允许拦截器基于连接执行必要的前置逻辑（如连接状态检查）。
     * @return 一个 {@link ExecutionControl} 枚举值：
     * <ul>
     *   <li>{@code PROCEED} 表示继续执行 SQL 或后续拦截器。</li>
     *   <li>{@code SKIP} 表示跳过 SQL 的实际执行，但仍会进入 {@link #afterExecution} 逻辑。</li>
     * </ul>
     */
    ExecutionControl beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection);

    /**
     * 当 {@link #beforeExecution} 方法返回 {@code SKIP} 时调用，用于生成跳过数据库操作的结果。
     * <p>
     * 通过该方法，拦截器可以根据业务逻辑返回一个自定义结果，而无需真正执行 SQL。
     * </p>
     *
     * @param sqlStatementWrapper 包含原始 SQL 和参数的包装对象。
     * @param connection          数据库连接对象，用于获取上下文信息。
     * @param <R>                 返回结果的类型。
     * @return 返回模拟的执行结果，允许开发者在逻辑中生成适合的返回值。默认返回 {@code null}。
     */
    default <R> R retrieveSkippedResult(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        return null;
    }

    /**
     * 在 SQL 执行完成或发生异常后执行的拦截逻辑。
     * <p>
     * 此方法提供一个统一的回调，无论 SQL 正常执行完成或因异常中断均会触发。
     * 可用于记录日志、清理资源或统计执行结果等操作。
     * </p>
     *
     * @param preparedSql 执行的 SQL 语句及其参数的预处理对象。可以用于记录或分析 SQL。
     * @param applyResult SQL 执行后的返回结果。如果 SQL 未实际执行，可能为 {@code null}。
     * @param exception   执行过程中可能抛出的异常。如果没有异常则为 {@code null}。
     */
    void afterExecution(PreparedSql preparedSql, Object applyResult, Exception exception);

    /**
     * 获取拦截器的执行优先级。
     * <p>
     * 拦截器按 {@code getOrder()} 方法返回的值进行排序，值越小优先级越高。
     * 可用于定义多个拦截器的执行顺序。
     * </p>
     *
     * @return 一个整数值，表示拦截器的优先级。默认值为 0。
     */
    default int getOrder() {
        return 0;
    }
}
