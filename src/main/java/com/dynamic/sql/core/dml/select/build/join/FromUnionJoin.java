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


import com.dynamic.sql.enums.UnionType;

public class FromUnionJoin extends FromJoin {
    private final UnionJoin unionJoin;
    private final UnionType unionType;

    public FromUnionJoin(UnionJoin nestedJoin, UnionType unionType) {
        super((Class<?>) null, nestedJoin.getTableAlias());
        this.unionJoin = nestedJoin;
        this.unionType = unionType;
    }

    public UnionJoin getUnionJoin() {
        return unionJoin;
    }

    public UnionType getUnionType() {
        return unionType;
    }

    public void setTableClass(Class<?> tableClass) {
        super.tableClass = tableClass;
    }
}
