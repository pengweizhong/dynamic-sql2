package com.dynamic.sql.core.condition;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.aggregate.Max;
import com.dynamic.sql.entites.User;
import org.junit.jupiter.api.Test;

import java.util.List;

class NestedConditionTest extends InitializingContext {

    @Test
    void andEqualTo() {
        User one = sqlContext.select().allColumn()
                .from(User.class)
                .groupBy(User::getUserId)
                .where(whereCondition -> whereCondition.andEqualTo(User::getUserId,
                        select -> {
                            select.column(new Max(User::getUserId)).from(User.class);
                        }))
                .fetch()
                .toOne();
        System.out.println(one);
    }

    @Test
    void orEqualTo() {
    }

    @Test
    void andNotEqualTo() {
    }

    @Test
    void orNotEqualTo() {
        List<User> list = sqlContext.select()
                .column(new Column(User::getUserId).multiply(100),"userId")
                .from(User.class)
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void andIn() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> whereCondition.andIn(User::getUserId, select -> select.column(User::getUserId).from(User.class)))
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void andExists() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> whereCondition.andExists(select -> select.column(User::getUserId).from(User.class)))
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }
}