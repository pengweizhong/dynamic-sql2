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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SqlInterceptorChain implements SqlInterceptor {

    private static final Logger log = LoggerFactory.getLogger(SqlInterceptorChain.class);
    // 存储多个拦截器
    private List<SqlInterceptor> interceptors = new ArrayList<>();

    // 私有构造函数
    private SqlInterceptorChain() {
    }

    // 静态内部类用于单例的延迟初始化
    private static class Holder {
        private static final SqlInterceptorChain INSTANCE = new SqlInterceptorChain(); // 初始为空列表
    }

    // 获取单例实例
    public static SqlInterceptorChain getInstance() {
        return Holder.INSTANCE;
    }

    public void addInterceptor(SqlInterceptor interceptor) {
        if (interceptors.contains(interceptor)) {
            return;
        }
        this.interceptors.add(interceptor);
        this.interceptors.sort(Comparator.comparingInt(SqlInterceptor::getOrder));
    }

    @Override
    public ExecutionControl beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        // 依次执行拦截器
        for (SqlInterceptor interceptor : interceptors) {
            // 如果某个拦截器返回false，直接中断链条
            ExecutionControl executionControl = interceptor.beforeExecution(sqlStatementWrapper, connection);
            if (executionControl == ExecutionControl.SKIP) {
                log.trace("The SQL interceptor link interrupts execution, located at: {}", interceptor.getClass().getSimpleName());
                return executionControl;
            }
        }
        // 只有所有拦截器都通过时，才返回true
        return ExecutionControl.PROCEED;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <R> R retrieveSkippedResult(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        for (SqlInterceptor interceptor : interceptors) {
            Object skippedResult = interceptor.retrieveSkippedResult(sqlStatementWrapper, connection);
            // 如果某个拦截器返回了结果，直接中断链条
            if (skippedResult != null) {
                log.trace("The SQL interceptor link interrupts execution, located at: {}", interceptor.getClass().getSimpleName());
                return (R) skippedResult;
            }
        }
        return null;
    }

    @Override
    public void afterExecution(PreparedSql preparedSql, Object applyResult, Exception exception) {
        // 依次执行每个拦截器的afterExecution方法
        for (SqlInterceptor interceptor : interceptors) {
            interceptor.afterExecution(preparedSql, applyResult, exception);
        }
    }

    public SqlInterceptor getInterceptor(Class<? extends SqlInterceptor> sqlInterceptorClass) {
        if (sqlInterceptorClass == null) {
            return null;
        }
        for (SqlInterceptor interceptor : interceptors) {
            if (interceptor.getClass().equals(sqlInterceptorClass)) {
                return interceptor;
            }
        }
        return null;
    }
}
