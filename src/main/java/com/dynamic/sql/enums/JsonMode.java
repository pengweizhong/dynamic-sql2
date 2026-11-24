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

public enum JsonMode {
    /**
     * 写入模式
     * json对象为空的属性，会写入到数据库中
     */
    SERIALIZE_WRITE_NULLS,
    /**
     * 写入模式（默认值）
     * json对象为空的属性，不会写入到数据库中
     */
    SERIALIZE_WRITE_NO_NULLS,
    ;
}
