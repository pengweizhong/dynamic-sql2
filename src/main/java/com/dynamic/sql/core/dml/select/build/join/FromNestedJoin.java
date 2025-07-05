/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select.build.join;


public class FromNestedJoin extends FromJoin {
    private final NestedJoin nestedJoin;

    public FromNestedJoin(NestedJoin nestedJoin) {
        super((Class<?>) null, nestedJoin.getTableAlias());
        this.nestedJoin = nestedJoin;
    }

    public NestedJoin getNestedJoin() {
        return nestedJoin;
    }
}
