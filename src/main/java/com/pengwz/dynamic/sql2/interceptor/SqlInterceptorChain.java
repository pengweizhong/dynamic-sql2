package com.pengwz.dynamic.sql2.interceptor;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
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
        Collections.sort(this.interceptors, Comparator.comparingInt(SqlInterceptor::getOrder));
    }

    @Override
    public boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper) {
        // 依次执行拦截器
        for (SqlInterceptor interceptor : interceptors) {
            // 如果某个拦截器返回false，直接中断链条
            boolean proceed = interceptor.beforeExecution(sqlStatementWrapper);
            if (!proceed) {
                log.debug("The SQL interceptor link interrupts execution, located at: {}", interceptor.getClass().getCanonicalName());
                return false;
            }
        }
        // 只有所有拦截器都通过时，才返回true
        return true;
    }

    @Override
    public void afterExecution(PreparedSql preparedSql, Exception exception) {
        // 依次执行每个拦截器的afterExecution方法
        for (SqlInterceptor interceptor : interceptors) {
            interceptor.afterExecution(preparedSql, exception);
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
