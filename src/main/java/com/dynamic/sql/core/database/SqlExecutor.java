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


import com.dynamic.sql.core.dml.delete.DeleteHandler;
import com.dynamic.sql.core.dml.insert.InsertHandler;
import com.dynamic.sql.core.dml.select.SelectHandler;
import com.dynamic.sql.core.dml.update.UpdateHandler;
import com.dynamic.sql.model.ColumnMetaData;
import com.dynamic.sql.model.TableMetaData;

import java.util.List;

public interface SqlExecutor extends SelectHandler, InsertHandler, DeleteHandler, UpdateHandler {
    default List<TableMetaData> getAllTableMetaData(String catalog, String schemaPattern, String tableNamePattern, String[] tableTypes) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    default List<ColumnMetaData> getAllColumnMetaData(String catalog, String schemaPattern, String tableNamePattern, String columnNamePattern) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}