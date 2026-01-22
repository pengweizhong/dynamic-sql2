/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.enums.SqlExecuteType;

public class SqlLogContext {
    //执行前的参数
    private final SqlExecuteType sqlExecuteType;
    private final String dataSourceName;
    private final PreparedSql preparedSql;
    private final boolean intercepted;
    //后面跟踪的参数，都是执行后的结果
    private long startTime;
    private long endTime;
    private Object rawResult;

    //执行前的参数快照
    public SqlLogContext(SqlExecuteType type, String dataSourceName, PreparedSql preparedSql, boolean intercepted) {
        this.sqlExecuteType = type;
        this.dataSourceName = dataSourceName;
        this.preparedSql = preparedSql;
        this.intercepted = intercepted;
    }

    public SqlExecuteType getSqlExecuteType() {
        return sqlExecuteType;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public PreparedSql getPreparedSql() {
        return preparedSql;
    }

    public boolean isIntercepted() {
        return intercepted;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public Object getRawResult() {
        return rawResult;
    }

    public void setRawResult(Object rawResult) {
        this.rawResult = rawResult;
    }

}
