package com.pengwz.dynamic.sql2.utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class MapUtils {
    private MapUtils() {
    }

    @SuppressWarnings("unchecked")
    public static <K, V> Map<K, V> of(Object... keyValue) {
        Map<K, V> hashMap = new LinkedHashMap<>();
        if (keyValue == null || keyValue.length == 0) {
            return hashMap;
        }
        if (keyValue.length % 2 != 0) {
            throw new IllegalArgumentException("keyValue.length % 2 != 0");
        }
        for (int i = 0; i < keyValue.length; i += 2) {
            Object key = keyValue[i];
            Object value = keyValue[i + 1];
            hashMap.put((K) key, (V) value);
        }
        return hashMap;
    }
}
