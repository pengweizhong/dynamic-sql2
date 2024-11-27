package com.pengwz.dynamic.sql2.core.column.function;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.core.column.conventional.Column;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Max;
import com.pengwz.dynamic.sql2.entites.User;
import org.junit.jupiter.api.Test;

import java.util.List;

class AbstractColumFunctionTest extends InitializingContext {

    @Test
    void subtract() {
        List<User> list = sqlContext.select()
                .column(new Column(User::getUserId).subtract(10), "userId")
                .from(User.class)
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void testSubtract() {
        List<User> list = sqlContext.select()
                .column(new Column(User::getUserId).subtract(nestedSelect -> {
                    nestedSelect.column(new Max(User::getUserId)).from(User.class);
                }), "userId")
                .from(User.class)
                .where(whereCondition -> whereCondition.andGreaterThanOrEqualTo(User::getUserId, 1))
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void testSubtract1() {
        List<User> list = sqlContext.select()
                .column(new Column(User::getUserId).subtract(User::getStatus), "userId")
                .from(User.class)
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void multiply() {
    }

    @Test
    void testMultiply() {
    }

    @Test
    void testMultiply1() {
    }

    @Test
    void divide() {
    }

    @Test
    void testDivide() {
    }

    @Test
    void testDivide1() {
    }

    @Test
    void add() {
    }

    @Test
    void testAdd() {
    }

    @Test
    void testAdd1() {
    }

    @Test
    void getArithmeticSql() {
    }

    @Test
    void getArithmeticParameterBinder() {
    }
}