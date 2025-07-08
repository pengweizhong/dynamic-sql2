/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.pagination;

public enum DefaultPagePluginType implements PagePluginType {
    MYBATIS("MyBatis 插件分页"),
    DYNAMIC_SQL2("Dynamic-SQL2 内部分页");
    private final String description;

    DefaultPagePluginType(String description) {
        this.description = description;
    }

    public String description() {
        return description;
    }

    @Override
    public String getPluginName() {
        return name();
    }
}
