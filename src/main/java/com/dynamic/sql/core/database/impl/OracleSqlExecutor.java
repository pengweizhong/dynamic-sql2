/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database.impl;


import com.dynamic.sql.core.database.AbstractSqlExecutor;
import com.dynamic.sql.core.database.PreparedSql;
import com.dynamic.sql.core.database.RootExecutor;

import java.sql.Connection;
import java.util.List;
import java.util.Map;


public class OracleSqlExecutor extends AbstractSqlExecutor {

    public OracleSqlExecutor(Connection connection, PreparedSql preparedSql) {
        super(connection, preparedSql);
    }

    @Override
    public List<Map<String, Object>> executeQuery() {
        return RootExecutor.executeQuery(connection, preparedSql);
    }

    @Override
    public int insertSelective() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int insert() {
        return RootExecutor.executeInsert(connection, preparedSql);
    }

    @Override
    public int insertBatch() {
        return 0;
    }

    @Override
    public int insertMultiple() {
        return 0;
    }

    @Override
    public int deleteByPrimaryKey() {
        return 0;
    }

    @Override
    public int delete() {
        return 0;
    }

    @Override
    public int updateByPrimaryKey() {
        return 0;
    }

    @Override
    public int updateSelectiveByPrimaryKey() {
        return 0;
    }

    @Override
    public int update() {
        return 0;
    }

    @Override
    public int updateSelective() {
        return 0;
    }

    @Override
    public int upsert() {
        return 0;
    }

    @Override
    public int upsertSelective() {
        return 0;
    }

    @Override
    public int upsertMultiple() {
        return 0;
    }
}
