/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.utils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class MapUtils {
    private MapUtils() {
    }

    /**
     * 使用可变参数快速构建一个 {@link LinkedHashMap} 实例。
     * <p>
     * 示例：<br>
     * {@code Map<String, Object> map = MapUtils.of("key1", val1, "key2", val2);}
     * <p>
     * 如果参数个数不是偶数，将抛出 {@link IllegalArgumentException}。
     *
     * @param keyValue 键值对，可变参数，按 key1, value1, key2, value2... 顺序传入
     * @param <K>      键类型
     * @param <V>      值类型
     * @return 构建完成的 {@link LinkedHashMap}
     * @throws IllegalArgumentException 如果参数个数不是偶数
     */
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

    /**
     * 判断 Map 是否为空（null 或无元素）。
     */
    public static <K, V> boolean isEmpty(Map<K, V> map) {
        return map == null || map.isEmpty();
    }

    /**
     * 判断 Map 是否非空。
     */
    public static <K, V> boolean isNotEmpty(Map<K, V> map) {
        return !isEmpty(map);
    }

    /**
     * 从指定的 Map 中获取 key 对应的值，如果不存在则使用给定的映射函数计算并插入。
     * <p>
     * 此方法是对 {@link Map#computeIfAbsent(Object, Function)} 的封装，保证：
     * <ul>
     *     <li>如果 key 已存在，直接返回对应的值。</li>
     *     <li>如果 key 不存在，调用 {@code mappingFunction} 计算新值并放入 Map，然后返回该值。</li>
     * </ul>
     * </p>
     *
     * @param map             目标 Map，不能为空。
     * @param key             要查找或插入的键。
     * @param mappingFunction 当 key 不存在时，用于生成新值的函数。
     * @param <K>             键的类型。
     * @param <V>             值的类型。
     * @return key 对应的值，如果不存在则返回新计算的值。
     *
     * <p><b>示例：</b></p>
     * <pre>{@code
     * Map<String, Integer> cache = new HashMap<>();
     * Integer length = computeIfAbsent(cache, "hello", String::length);
     * // 如果 "hello" 不存在，则插入 5；否则返回已有值。
     * }</pre>
     */
    public static <K, V> V computeIfAbsent(Map<K, V> map, K key, Function<K, V> mappingFunction) {
        V value = map.get(key);
        if (value != null) {
            return value;
        }
        return map.computeIfAbsent(key, mappingFunction);
    }

}
