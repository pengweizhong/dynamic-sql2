package com.dynamic.sql.utils;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class CollectionUtilsTest {

    @Test
    void isEmpty() {
    }

    @Test
    void isNotEmpty() {
    }

    @Test
    void partition() {
        List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7);
        List<List<Integer>> parts = CollectionUtils.partition(list, 3);
        System.out.println(parts.size());
        parts.forEach(System.out::println);
    }
}