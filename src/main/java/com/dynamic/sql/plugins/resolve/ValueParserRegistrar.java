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
 * 解析器注册器，用于初始化并注册所有可用的 ValueParser。
 */
public class ValueParserRegistrar {

    /**
     * 注册用户自定义解析器
     */
    public void register(ValueParser parser) {
        ValueParserManager.register(parser);
    }
}
