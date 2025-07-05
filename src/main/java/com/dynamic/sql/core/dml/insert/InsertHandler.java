/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.insert;

public interface InsertHandler {

    int insertSelective();

    int insert();

    int insertBatch();

    int insertMultiple();

    int upsert();

    int upsertSelective();

    int upsertMultiple();
}
