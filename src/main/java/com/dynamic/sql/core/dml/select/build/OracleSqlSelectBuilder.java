/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build;


import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

public class OracleSqlSelectBuilder extends GenericSqlSelectBuilder {

    public OracleSqlSelectBuilder(SelectSpecification selectSpecification, Map<String, TableAliasMapping> aliasTableMap) {
        super(selectSpecification, aliasTableMap);
    }

    @Override
    public String parseLimit() {
        throw new UnsupportedOperationException("Oracle SQL uses ROWNUM for pagination, which is handled differently.");
    }
}
