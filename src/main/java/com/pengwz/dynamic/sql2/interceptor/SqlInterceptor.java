package com.pengwz.dynamic.sql2.interceptor;

import com.pengwz.dynamic.sql2.core.database.PreparedSql;
import com.pengwz.dynamic.sql2.core.dml.SqlStatementWrapper;

import java.sql.Connection;

/**
 * SQL 拦截器接口，用于在 SQL 执行过程中提供拦截和处理功能。
 * <p>
 * 实现该接口的类可以在 SQL 执行前后执行自定义逻辑，例如日志记录、权限控制、动态修改 SQL
 * 或者进行统计等操作。
 * </p>
 */
public interface SqlInterceptor {

    /**
     * 在 SQL 执行之前执行的逻辑。
     *
     * @param sqlStatementWrapper SQL 语句包装对象，包含原始 SQL 及其参数。
     * @param connection          数据库连接，用于执行 SQL 操作。
     * @return 如果返回 {@code true}，表示继续执行后续拦截器或 SQL 执行；如果返回 {@code false}，
     * 则中断后续操作。
     */
    boolean beforeExecution(SqlStatementWrapper sqlStatementWrapper, Connection connection);

    /**
     * 当 {@code beforeExecution} 返回 {@code false} 时调用，返回跳过数据库交互的结果。
     *
     * @param sqlStatementWrapper SQL 语句包装对象，包含原始 SQL 及其参数。
     * @param connection          可用数据库连接
     * @param <R>                 返回结果的类型。
     * @return 允许返回任意兼容的对象类型
     */
    default <R> R retrieveSkippedResult(SqlStatementWrapper sqlStatementWrapper, Connection connection) {
        return null;
    }

    /**
     * 在 SQL 执行之后执行的逻辑。
     *
     * @param preparedSql 执行的 SQL 语句及其参数的预处理对象。
     * @param applyResult sql执行后的对象
     * @param exception   执行过程中可能抛出的异常，如果没有异常则为 {@code null}。
     */
    void afterExecution(PreparedSql preparedSql, Object applyResult, Exception exception);

    /**
     * 获取拦截器的执行顺序。
     *
     * @return 返回一个整数值，值越小优先级越高。默认返回 0。
     */
    default int getOrder() {
        return 0;
    }
}
