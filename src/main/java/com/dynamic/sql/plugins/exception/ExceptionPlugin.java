/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.exception;

import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.dml.SqlStatementWrapper;
import com.dynamic.sql.interceptor.ExecutionControl;
import com.dynamic.sql.interceptor.SqlInterceptor;
import com.dynamic.sql.utils.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

/**
 * {@code ExceptionPlugin} 是 Dynamic SQL 引擎中的异常处理插件，实现 {@link SqlInterceptor} 接口。
 * <p>
 * 该插件会在 SQL 执行后自动拦截并分析异常信息，针对常见的 SQL 异常码，输出更友好的错误提示日志，
 * 以便于开发和调试。
 * </p>
 *
 * <p>
 * 使用方式：注册该插件并提供 {@link SqlErrorHint} 实现类，即可自动输出友好的 SQL 错误信息。
 * </p>
 *
 * <pre>{@code
 * SqlInterceptor exceptionPlugin = new ExceptionPlugin(new DefaultSqlErrorHint());
 * }</pre>
 */
public class ExceptionPlugin implements SqlInterceptor {
    private static final Logger log = LoggerFactory.getLogger(ExceptionPlugin.class);
    private final SqlErrorHint sqlErrorHint;

    public ExceptionPlugin(SqlErrorHint sqlErrorHint) {
        this.sqlErrorHint = sqlErrorHint;
    }

    @Override
    public ExecutionControl beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        return ExecutionControl.PROCEED;
    }

    @Override
    public void afterExecution(PreparedSql preparedSql, Object applyResult, Exception exception) {
        if (exception == null) {
            return;
        }
        if (exception instanceof SQLException) {
            fillSqlExceptionDescription(preparedSql.getSql(), (SQLException) exception);
            return;
        }
        Throwable cause = exception.getCause();
        while (cause != null) {
            if (cause instanceof SQLException) {
                fillSqlExceptionDescription(preparedSql.getSql(), (SQLException) cause);
                return;
            }
            cause = cause.getCause();
        }
    }

    private void fillSqlExceptionDescription(String sql, SQLException sqlException) {
        Map<String, String> errorHints = sqlErrorHint.getErrorHints();
        if (MapUtils.isEmpty(errorHints)) {
            return;
        }
        String sqlState = sqlException.getSQLState();
        int errorCode = sqlException.getErrorCode();
        String errorHintMsg = errorHints.get(sqlState);
        if (errorHintMsg != null) {
            log.error("{}, 原因：{}，参考SQL: {}", errorHintMsg, sqlException.getMessage(), sql);
            return;
        }
        log.error("SQL执行失败！errorCode: {}, 原因：{}，参考SQL: {}", errorCode, sqlException.getMessage(), sql);
    }

    @Override
    public int getOrder() {
        return -666;
    }
}
