/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.debugs;

import com.dynamic.sql.enums.LogLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlDebugger {
    private static final Logger log = LoggerFactory.getLogger(SqlDebugger.class);

    private SqlDebugger() {
    }

    /**
     * 判断指定的日志级别在当前 SLF4J 配置下是否不可用。
     *
     * <p>该方法是 {@link #isLogLevelEnabled(LogLevel)} 的反义方法，
     * 用于快速判断某个日志级别是否被禁用。</p>
     *
     * @param level 日志级别
     * @return 若当前日志框架未启用该级别，则返回 true；否则返回 false
     */
    public static boolean isLogLevelDisabled(LogLevel level) {
        return !isLogLevelEnabled(level);
    }

    /**
     * 判断指定的日志级别在当前 SLF4J 配置下是否可用。
     *
     * <p>该方法用于在打印 SQL 之前进行日志级别判断，
     * 避免不必要的字符串拼接和日志格式化开销，从而提升性能。</p>
     *
     * @param level 日志级别
     * @return 当前日志框架是否启用了该级别
     */
    public static boolean isLogLevelEnabled(LogLevel level) {
        switch (level) {
            case TRACE:
                return log.isTraceEnabled();
            case DEBUG:
                return log.isDebugEnabled();
            case INFO:
                return log.isInfoEnabled();
            case WARN:
                return log.isWarnEnabled();
            case ERROR:
                return log.isErrorEnabled();
            default:
                return false;
        }
    }

    /**
     * 根据指定的日志级别输出 SQL 调试信息。
     *
     * <p>该方法会将 SQL 日志路由到 SLF4J 对应的日志方法，例如：</p>
     * <ul>
     *     <li>{@code TRACE → log.trace()}</li>
     *     <li>{@code DEBUG → log.debug()}</li>
     *     <li>{@code INFO → log.info()}</li>
     *     <li>{@code WARN → log.warn()}</li>
     *     <li>{@code ERROR → log.error()}</li>
     * </ul>
     *
     * <p>如果传入的日志级别未识别，则默认使用 DEBUG 级别输出。</p>
     *
     * @param level 日志级别
     * @param text  日志内容模板
     * @param args  模板参数
     */
    public static void printSql(LogLevel level, String text, Object... args) {
        switch (level) {
            case TRACE:
                log.trace(text, args);
                break;
            case DEBUG:
                log.debug(text, args);
                break;
            case INFO:
                log.info(text, args);
                break;
            case WARN:
                log.warn(text, args);
                break;
            case ERROR:
                log.error(text, args);
                break;
            default:
                log.debug(text, args);
        }
    }

}
