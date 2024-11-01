package com.pengwz.dynamic.sql2.core.dml;

import com.pengwz.dynamic.sql2.core.placeholder.ParameterBinder;

import java.util.ArrayList;
import java.util.List;

/**
 * SQL 语句包装器类，用于处理 SQL 文本和参数绑定，以支持动态 SQL 执行。
 */
public class SqlStatementWrapper {
    /*
     * 数据源名称，用于指定执行该 SQL 语句的数据源。
     */
    private final String dataSourceName;
    /*
     * 原始 SQL 语句，通过 StringBuilder 构建，支持动态修改。
     */
    private final StringBuilder rawSql;
    /*
     * 参数绑定器列表，保存 SQL 参数的绑定信息。
     * 在批量执行时，可添加多个 ParameterBinder 实例以支持批量处理。
     */
    private final List<ParameterBinder> parameterBinders = new ArrayList<>();

    private BatchType batchType;

    /**
     * 构造一个新的 SqlStatementWrapper 实例。
     *
     * @param dataSourceName  数据源名称，用于指定执行 SQL 语句的数据源
     * @param rawSql          SQL 语句，以 StringBuilder 格式传入，以支持动态修改
     * @param parameterBinder 初始参数绑定器，用于将参数绑定到 SQL 语句
     */
    public SqlStatementWrapper(String dataSourceName, StringBuilder rawSql, ParameterBinder parameterBinder) {
        this.dataSourceName = dataSourceName;
        this.rawSql = rawSql;
        this.parameterBinders.add(parameterBinder);
    }

    /**
     * 构造一个新的 SqlStatementWrapper 实例。
     *
     * @param dataSourceName 数据源名称，用于指定执行 SQL 语句的数据源
     * @param rawSql         SQL 语句，以 StringBuilder 格式传入，以支持动态修改
     */
    public SqlStatementWrapper(String dataSourceName, StringBuilder rawSql) {
        this.dataSourceName = dataSourceName;
        this.rawSql = rawSql;
    }

    /**
     * 获取原始 SQL 语句。
     *
     * @return 原始 SQL 语句的 StringBuilder 对象
     */
    public StringBuilder getRawSql() {
        return rawSql;
    }

    /**
     * 获取第一个参数绑定器，用于非批量操作。
     *
     * @return 参数绑定器列表中的第一个 ParameterBinder 实例
     */
    public ParameterBinder getParameterBinder() {
        return parameterBinders.get(0);
    }

    /**
     * 获取所有参数绑定器，用于批量操作（例如批量插入）。
     *
     * @return 包含所有 ParameterBinder 实例的列表
     */
    public List<ParameterBinder> getBatchParameterBinders() {
        return parameterBinders;
    }

    /**
     * 添加新的参数绑定器，用于批量操作。
     *
     * @param parameterBinder 要添加的 ParameterBinder 实例
     */
    public void addBatchParameterBinder(ParameterBinder parameterBinder) {
        this.parameterBinders.add(parameterBinder);
    }

    /**
     * 获取数据源名称。
     *
     * @return 数据源名称
     */
    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setBatchType(BatchType batchType) {
        this.batchType = batchType;
    }

    /**
     * 是否批量语句
     */
    public BatchType getBatchType() {
        return batchType;
    }

    public enum BatchType {
        BATCH, MULTIPLE;
    }
}
