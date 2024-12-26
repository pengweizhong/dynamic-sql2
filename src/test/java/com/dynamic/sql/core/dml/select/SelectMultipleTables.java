package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.Order;
import com.dynamic.sql.entites.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

public class SelectMultipleTables extends InitializingContext {

    @Test
    void selectMultipleTables() {
        List<Map> list = sqlContext.select()
                .allColumn()
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void selectMultipleTables2() {
        List<Map> list = sqlContext.select()
                .allColumn()
                .column(User::getUserId)
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void selectMultipleTables3() {
        List<Map> list = sqlContext.select()
                .column(User::getUserId)
                .allColumn()
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void selectMultipleTables4() {
        List<Map> list = sqlContext.select()
                .column(User::getUserId)
                .allColumn()
                .column(Order::getOrderId)
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void selectMultipleTables5() {
        List<Map> list = sqlContext.select()
                .column(User::getUserId)
                .column(Order::getOrderId)
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void selectMultipleTables6() {
        List<Map> list = sqlContext.select()
                .allColumn(User.class)
                .column(Order::getOrderId)
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void selectMultipleTables7() {
        List<Map> list = sqlContext.select()
                .allColumn(User.class)
                .allColumn(Order.class)
                .column(Order::getOrderId)
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

}
