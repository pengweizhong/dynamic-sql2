package com.dynamic.sql.utils;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MapUtilsTest {

    @Test
    void of() {
        Map<Object, Object> objectObjectMap = MapUtils.of("男", 1, "女", 2);
        System.out.println(objectObjectMap);
    }

    @Test
    void of2() {
        try {
            Map<Object, Object> objectObjectMap = MapUtils.of("男", 1, "女");
            System.out.println(objectObjectMap);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void isEmpty() {
    }

    @Test
    void isNotEmpty() {
    }

    @Test
    void computeIfAbsent() {
    }

    @Test
    void invert() {
        // 1. 正常情况
        Map<String, Integer> map = new LinkedHashMap<>();
        map.put("a", 1);
        map.put("b", 2);
        map.put("c", 3);

        Map<Integer, String> inverted = MapUtils.invert(map);
        assertEquals("a", inverted.get(1));
        assertEquals("b", inverted.get(2));
        assertEquals("c", inverted.get(3));
        assertEquals(3, inverted.size());

        // 2. 重复值（后者覆盖前者）
        map.put("d", 2); // value=2 已存在
        inverted = MapUtils.invert(map);
        assertEquals("d", inverted.get(2)); // 后者覆盖前者
        assertEquals(3, inverted.size());   // size 仍然是 3

        // 3. 空 Map
        Map<String, String> emptyMap = Collections.emptyMap();
        Map<String, String> invertedEmpty = MapUtils.invert(emptyMap);
        assertTrue(invertedEmpty.isEmpty());

        // 4. null Map
        Map<String, String> invertedNull = MapUtils.invert(null);
        assertTrue(invertedNull.isEmpty());
    }
}