/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.database.parser;


import com.dynamic.sql.core.Fn;

public interface InsertParser {

    void insertSelective(Fn<?, ?>[] forcedFields);

    void insert();

    void insertBatch();

    void insertMultiple();

    void upsert(Fn<?, ?>[] forcedFields);

    void upsertMultiple();
}