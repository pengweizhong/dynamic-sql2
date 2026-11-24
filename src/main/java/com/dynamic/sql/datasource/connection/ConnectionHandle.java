/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.datasource.connection;


import com.dynamic.sql.utils.SqlUtils;

import javax.sql.DataSource;
import java.sql.Connection;

@FunctionalInterface
public interface ConnectionHandle {
    Connection getConnection(DataSource dataSource);

    default void releaseConnection(DataSource dataSource, Connection connection) {
        SqlUtils.close(connection, null, null);
    }
}
