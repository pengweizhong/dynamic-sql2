package com.pengwz.dynamic.sql2.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectUtils {

    private ReflectUtils() {
    }

    /**
     * 获取指定类中的所有字段
     *
     * @param clazz          指定类
     * @param filterModifier 如果需要过滤类型，建议使用{@link Modifier}
     * @return 返回字段集合
     */
    public static List<Field> getAllFields(Class<?> clazz, int... filterModifier) {
        List<Field> fields = new ArrayList<>();
        collectFields(clazz, fields, filterModifier);
        return fields;
    }

    private static void collectFields(Class<?> clazz, List<Field> fields, int... filterModifier) {
        if (clazz == null || clazz.equals(Object.class)) {
            return;
        }
        // 获取当前类的所有字段
        Field[] declaredFields = clazz.getDeclaredFields();
        for (Field field : declaredFields) {
            // 过滤字段
            if (filterModifier == null || filterModifier.length == 0) {
                fields.add(field);
                continue;
            }
            int modifiers = field.getModifiers();
            if (Arrays.stream(filterModifier).anyMatch(mod -> modifiers == mod)) {
                continue;
            }
            fields.add(field);
        }
        // 递归处理父类
        collectFields(clazz.getSuperclass(), fields);
    }

}
