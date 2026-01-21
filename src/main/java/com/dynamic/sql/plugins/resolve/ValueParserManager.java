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

import java.util.ArrayList;
import java.util.List;

public class ValueParserManager {
    protected static final List<ValueParser> parsers = new ArrayList<>();

    protected ValueParserManager() {
    }

    /**
     * 注册解析器
     */
    protected static void register(ValueParser parser) {
        if (parsers.contains(parser)) {
            return;
        }
        parsers.add(parser);
    }

    /**
     * 解析入口
     */
    public static String resolve(String text) {
        for (ValueParser parser : parsers) {
            if (parser.supports(text)) {
                return parser.getValueResolver().resolve(text);
            }
        }
        // 没有解析器支持，返回原字符串
        return text;
    }
}
