package com.dynamic.sql.core.column.function.aggregate;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.Order;
import com.dynamic.sql.entites.User;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

class AggregateFunctionTest extends InitializingContext {

    @Test
    void max() {
        List<Map> list = sqlContext.select()
                .column(new Max(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void max2() {
        List<Map> list = sqlContext.select()
                .column(new Max(User::getRegistrationDate))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void min() {
        List<Map> list = sqlContext.select()
                .column(new Min(User::getRegistrationDate))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void min2() {
        List<Map> list = sqlContext.select()
                .column(new Min(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Map.class)
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void count() {
        Long count = sqlContext.select()
                .column(new Count(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(Long.class)
                .toOne();
        System.out.println(count);
    }

    @Test
    void count2() {
        BigDecimal count = sqlContext.select()
                .column(new Count(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(BigDecimal.class)
                .toOne();
        System.out.println(count);
    }

    @Test
    void count3() {
        AtomicInteger count = sqlContext.select()
                .column(new Count(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(AtomicInteger.class)
                .toOne();
        System.out.println(count);
    }

    @Test
    void count4() {
        AtomicBoolean count = sqlContext.select()
                .column(new Count(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(AtomicBoolean.class)
                .toOne();
        System.out.println(count);
    }

    @Test
    void sum() {
        AtomicLong sum = sqlContext.select()
                .column(new Sum(User::getUserId))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(AtomicLong.class)
                .toOne();
        System.out.println(sum);
    }

    @Test
    void sum2() {
        BigDecimal sum = sqlContext.select()
                .column(new Sum(User::getRegistrationDate))
                .from(User.class)
                .join(Order.class, on -> on.andEqualTo(User::getUserId, Order::getUserId))
                .fetch(BigDecimal.class)
                .toOne();
        System.out.println(sum);
    }

}