/*
 * Copyright (c) 2024 PengWeizhong.
 * All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.context.properties.SqlLogProperties;

/**
 * SQL 日志打印接口。
 *
 * <p>该接口定义了 SQL 执行前后日志输出的扩展点，允许用户自定义日志格式、
 * 输出方式（控制台、文件、JSON 等）以及日志级别。
 *
 * <p><strong>调用时机：</strong></p>
 * <ul>
 *     <li>beforeSql：SQL 预编译与参数绑定完成后、执行前调用</li>
 *     <li>afterSql：SQL 执行完成后调用（包含执行耗时、返回结果等信息）</li>
 * </ul>
 *
 * <p><strong>目前定义的业务边界：</strong></p>
 * <ul>
 *     <li>SqlLogger 只负责“如何打印”</li>
 *     <li>SqlLogContext 提供 SQL、参数、耗时、结果等上下文信息</li>
 *     <li>SqlLogProperties 控制日志开关、级别、格式等配置</li>
 * </ul>
 *
 * <p>框架默认提供一个基础实现，用户可按需替换。</p>
 */
public interface SqlLogger {

    /**
     * SQL 执行前的日志输出。
     *
     * <p>通常用于打印 SQL 文本、参数、数据源名称等信息。
     * 此方法不会包含执行耗时与结果，因为 SQL 尚未执行。</p>
     *
     * @param props 日志配置属性
     * @param ctx   SQL 执行上下文（包含 SQL、参数、数据源等信息）
     */
    void beforeSql(SqlLogProperties props, SqlLogContext ctx);

    /**
     * SQL 执行后的日志输出。
     *
     * <p>通常用于打印执行结果、影响行数、返回集合大小、执行耗时等信息。
     * 此方法在 SQL 执行完成后调用，ctx 中包含完整的执行信息。</p>
     *
     * @param props 日志配置属性
     * @param ctx   SQL 执行上下文（包含执行耗时、返回结果等信息）
     */
    void afterSql(SqlLogProperties props, SqlLogContext ctx);
}
