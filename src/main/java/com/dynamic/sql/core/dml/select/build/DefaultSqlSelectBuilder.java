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


import com.dynamic.sql.core.Version;
import com.dynamic.sql.enums.SqlDialect;

public class DefaultSqlSelectBuilder extends SqlSelectBuilder {

    public DefaultSqlSelectBuilder(SelectSpecification selectSpecification,
                                   Version version,
                                   SqlDialect sqlDialect,
                                   String dataSourceName) {
        super(selectSpecification, version, sqlDialect, dataSourceName);
    }


    @Override
    protected void parseColumnFunction() {

    }

    @Override
    protected boolean parseFormTables() {
        return false;
    }

    @Override
    public String parseLimit() {
        return "";
    }
}
