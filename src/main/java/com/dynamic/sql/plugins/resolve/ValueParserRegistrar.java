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
