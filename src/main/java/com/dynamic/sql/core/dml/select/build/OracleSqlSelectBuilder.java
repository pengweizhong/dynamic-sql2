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


public class OracleSqlSelectBuilder extends GenericSqlSelectBuilder {

    public OracleSqlSelectBuilder(SelectSpecification selectSpecification) {
        super(selectSpecification);
    }

    @Override
    public void parseLimit() {
        //Oracle需要单独处理limit
        super.parseLimit();
    }
}
