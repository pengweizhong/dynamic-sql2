/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface SelectHandler {

    List<Map<String, Object>> executeQuery();

    <T> T selectByPrimaryKey(Class<T> entityClass, Object pkValue);

    <T> List<T> selectByPrimaryKey(Class<T> entityClass, Collection<?> pkValues);
}
