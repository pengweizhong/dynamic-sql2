package com.dynamic.sql.plugins.resolve;

import java.util.ArrayList;
import java.util.List;

public class ValueParserManager {
    private static final List<ValueParser> parsers = new ArrayList<>();

    private ValueParserManager() {
    }

    /**
     * 注册解析器
     */
    public static void register(ValueParser parser) {
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
