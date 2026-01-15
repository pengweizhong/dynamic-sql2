package com.dynamic.sql.plugins.resolve;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一个简单的占位符解析器，支持 Spring @Value 类似的语法：
 * <p>
 * ${key}<br>
 * ${key:}<br>
 * ${key:defaultValue}<br>
 * <p>
 * 解析逻辑：<br>
 * 1. 从配置 Map 中查找 key 对应的值；<br>
 * 2. 如果不存在，则使用 defaultValue；<br>
 * 3. 如果 defaultValue 也不存在(可为空)，则抛异常；<br>
 * 4. 支持递归解析，即 value 中仍然可以包含占位符；<br>
 * <p>
 * 示例：
 * config = { "url" -> "http://example.com" }<br>
 * resolve("${url:default}")  => "http://example.com"<br>
 * resolve("${missing:default}") => "default"
 */
public class ValueResolver {

    /**
     * 配置源，用于根据 key 查找对应的值
     */
    private final Map<String, String> config;

    public ValueResolver(Map<String, String> config) {
        this.config = config;
    }


    /**
     * 占位符匹配模式：
     * <p>
     * ${key}
     * ${key:default}
     * <p>
     * 解释：
     * \\$\\{           匹配 "${"<br>
     * ([^:{}]+)        捕获 key（不能包含 : 或 {}）<br>
     * (?::([^{}]*))?   可选的 ":default" 部分<br>
     * }                匹配结束的 "}"<br>
     */
    private static final Pattern PATTERN = Pattern.compile("\\$\\{([^:{}]+)(?::([^{}]*))?}");

    /**
     * 解析字符串中的占位符。
     *
     * @param text 输入文本，可能包含占位符
     * @return 替换后的文本
     */
    public String resolve(String text) {
        Matcher matcher = PATTERN.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            // group(1) = key
            String key = matcher.group(1);
            // group(2) = defaultValue（可能为 null）
            String defaultValue = matcher.group(2);
            // 从配置中取值，取不到则使用默认值
            String value = config.getOrDefault(key, defaultValue);
            // 如果没有默认值也没有配置，则视为错误
            if (value == null) {
                throw new IllegalArgumentException("Missing config key: " + key);
            }
            // 支持递归解析，例如：
            // config.put("a", "${b}");
            // config.put("b", "value");
            value = resolve(value);
            // 将占位符替换为最终值
            matcher.appendReplacement(sb, Matcher.quoteReplacement(value));
        }
        // 追加剩余未匹配的文本
        matcher.appendTail(sb);
        return sb.toString();
    }

    /**
     * 获取配置源
     *
     * @return 配置源
     */
    public Map<String, String> getConfig() {
        return config;
    }
}
