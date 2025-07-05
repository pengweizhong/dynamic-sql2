/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database;


import com.dynamic.sql.core.dml.select.DefaultSelectHandler;

import java.sql.Connection;

public abstract class AbstractSqlExecutor extends DefaultSelectHandler implements SqlExecutor {

    protected Connection connection;
    protected PreparedSql preparedSql;

    protected AbstractSqlExecutor(Connection connection, PreparedSql parseSql) {
        this.connection = connection;
        this.preparedSql = parseSql;
    }
}
