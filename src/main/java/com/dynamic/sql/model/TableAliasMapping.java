/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.model;

/**
 * 表别名映射规则
 * <p>
 * 主要用于处理复杂的SQL查询场景，尤其是涉及嵌套JOIN操作时。
 * 通过该类，可以明确标识表的别名以及是否为嵌套JOIN，从而确保SQL语句的正确生成和执行。
 * </p>
 */
public class TableAliasMapping {
    private String alias;
    private boolean isNestedJoin;

    public TableAliasMapping() {
    }

    public TableAliasMapping(String alias, boolean isNestedJoin) {
        this.alias = alias;
        this.isNestedJoin = isNestedJoin;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public boolean isNestedJoin() {
        return isNestedJoin;
    }

    public void setIsNestedJoin(boolean nestedJoin) {
        isNestedJoin = nestedJoin;
    }

}
