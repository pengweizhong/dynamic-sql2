/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger.impl;

import com.dynamic.sql.context.properties.SqlLogProperties;
import com.dynamic.sql.plugins.logger.AbstractSqlLog;
import com.dynamic.sql.plugins.logger.SqlLogContext;
import com.dynamic.sql.plugins.logger.SqlLogger;

public class CloseSqlLogger extends AbstractSqlLog implements SqlLogger {

    @Override
    public void beforeSql(SqlLogProperties props, SqlLogContext ctx) {
        //nothing
    }

    @Override
    public void afterSql(SqlLogProperties props, SqlLogContext ctx) {
        //nothing
    }
}
