/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.context.properties;

import com.dynamic.sql.plugins.logger.SqlLogger;
import jdk.jfr.internal.LogLevel;

public class SqlLogProperties {
    /**
     * 是否打印 SQL
     */
    private boolean enabled = false;
    /**
     * 是否打印数据源名称
     */
    private boolean printDataSourceName = false;
    /**
     * 是否打印参数
     */
    private boolean printParameters = true;
    /**
     * 是否打印执行结果
     */
    private boolean printResult = true;
    /**
     * 是否打印执行耗时
     */
    private boolean printExecutionTime = false;
    /**
     * 日志级别（debug/info/trace）
     */
    private LogLevel level = LogLevel.DEBUG;
    /**
     * 自定义 Logger（可选，默认 DefaultSqlLogger）
     */
    private SqlLogger logger;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isPrintDataSourceName() {
        return printDataSourceName;
    }

    public void setPrintDataSourceName(boolean printDataSourceName) {
        this.printDataSourceName = printDataSourceName;
    }

    public boolean isPrintParameters() {
        return printParameters;
    }

    public void setPrintParameters(boolean printParameters) {
        this.printParameters = printParameters;
    }

    public boolean isPrintResult() {
        return printResult;
    }

    public void setPrintResult(boolean printResult) {
        this.printResult = printResult;
    }

    public boolean isPrintExecutionTime() {
        return printExecutionTime;
    }

    public void setPrintExecutionTime(boolean printExecutionTime) {
        this.printExecutionTime = printExecutionTime;
    }

    public LogLevel getLevel() {
        return level;
    }

    public void setLevel(LogLevel level) {
        this.level = level;
    }

    public SqlLogger getLogger() {
        return logger;
    }

    public void setLogger(SqlLogger logger) {
        this.logger = logger;
    }
}
