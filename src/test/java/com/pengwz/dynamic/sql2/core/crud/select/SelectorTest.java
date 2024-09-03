package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.column.function.Avg;
import com.pengwz.dynamic.sql2.core.column.function.Max;
import com.pengwz.dynamic.sql2.entites.SimpleUserEntity;
import org.junit.jupiter.api.Test;

import java.util.List;

class SelectorTest {
    @Test
    void test() {
        List<Object> list = Selector.instance()
                .allColumn()
                .from(SimpleUserEntity.class)
                .where(() -> 1 != 1)
                .toList();
        System.out.println(list);
    }

    @Test
    void test2() {
        List<Object> list = Selector.instance()
                .column(SimpleUserEntity::getAge)
                .from(SimpleUserEntity.class)
                .toList();
        System.out.println(list);
    }

    @Test
    void test3() {
        List<Object> list = Selector.instance()
                .column(new Avg(new Max(SimpleUserEntity::getAge)))
                .from(SimpleUserEntity.class)
                .toList();
        System.out.println(list);
    }
}