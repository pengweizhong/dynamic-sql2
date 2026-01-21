/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.resolve;

/**
 * 可选实现的 Value 解析接口。
 * 实现该接口的类可以提供自定义的 ValueResolver。
 */
public interface ValueParser {
    /**
     * 是否支持解析该文本
     */
    default boolean supports(String text) {
        return true;
    }

    /**
     * 返回一个 ValueResolver 实例，用于解析字符串。
     * 如果返回 null，则视为不支持解析。
     */
    ValueResolver getValueResolver();
}
