/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.enums;

public enum DDLType implements SqlExecuteType {
    // 创建数据库对象（如表、视图、索引、序列等）。
    CREATE,
    // 修改现有数据库对象（如添加列、修改列类型）。
    ALTER,
    // 删除数据库对象（如删除表、视图）。
    DROP,
    // 清空表数据但保留表结构。
    TRUNCATE,
    // 获取元数据（如表信息、列信息、索引信息）。
    GET_META,
    // 重命名数据库对象。
    RENAME,
    // 添加或修改注释（如表的备注）。
    COMMENT,

    ;

    @Override
    public String getType() {
        return this.name();
    }

}
