package com.dynamic.sql.plugins.resolve;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

class ValueResolverTest {
    @Test
    void testResolve() {
        Map<String, String> config = new HashMap<>();
        config.put("com.pengwz.profile.service.url", "http://real-url");
        config.put("inner.key", "${com.pengwz.profile.service.url:default}");
        config.put("testDatabase", "test_db_6666");
        ValueResolver parser = new ValueResolver(config);
        System.out.println(parser.resolve("${com.pengwz.profile.service.url:xxx}"));
        System.out.println(parser.resolve("${inner.key:kkkk}"));
        System.out.println(parser.resolve("${inner.key}"));
        System.out.println(parser.resolve("${inner.key2:}"));
//        System.out.println(parser.resolve("${inner.key3}"));
        System.out.println(parser.resolve("test_db"));
        System.out.println(parser.resolve("test_db_${testDatabase}"));
    }
}