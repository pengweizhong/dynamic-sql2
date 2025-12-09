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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CollectionUtils {
    private CollectionUtils() {
    }

    public static boolean isEmpty(Collection<?> collection) {
        return collection == null || collection.isEmpty();
    }

    public static boolean isNotEmpty(Collection<?> collection) {
        return !isEmpty(collection);
    }

    /**
     * 将集合按照指定大小分割为多个子集合。
     * <p>
     * 如果集合大小不足一个分组大小，则最后一个子集合的元素数量可能小于指定大小。
     * </p>
     *
     * @param collection 原始集合，若为 {@code null} 或为空集合，则返回空列表
     * @param size       每个子集合的最大长度，必须大于 0
     * @param <T>        集合元素类型
     * @return 分割后的子集合列表，每个子集合为一个独立的 {@link List}
     * @throws IllegalArgumentException 如果 size 小于等于 0
     *
     *  <pre>
     *      示例：
     * {@code
     * List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
     * List<List<Integer>> parts = CollectionUtils.partition(list, 3);
     * // parts = [[1,2,3], [4,5,6], [7]]
     * }
     *  </pre>
     */
    public static <T> List<List<T>> partition(Collection<T> collection, int size) {
        if (collection == null || collection.isEmpty()) {
            return new ArrayList<>();
        }
        if (size <= 0) {
            throw new IllegalArgumentException("分割大小必须大于 0: " + size);
        }
        List<T> list = (collection instanceof List) ? (List<T>) collection : new ArrayList<>(collection);
        int total = list.size();
        int groupCount = (total + size - 1) / size; // 向上取整
        List<List<T>> result = new ArrayList<>(groupCount);

        for (int i = 0; i < total; i += size) {
            int end = Math.min(i + size, total);
            result.add(new ArrayList<>(list.subList(i, end))); // 独立子集合
        }
        return result;
    }

}
