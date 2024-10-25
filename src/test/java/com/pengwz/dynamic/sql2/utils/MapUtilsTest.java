package com.pengwz.dynamic.sql2.utils;

import org.junit.jupiter.api.Test;

import java.util.Map;

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
}