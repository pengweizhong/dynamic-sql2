package com.dynamic.sql.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

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

    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

}
