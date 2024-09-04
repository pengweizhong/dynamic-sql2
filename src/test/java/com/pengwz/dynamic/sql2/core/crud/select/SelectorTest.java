package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.Alias;
import com.pengwz.dynamic.sql2.core.column.function.Avg;
import com.pengwz.dynamic.sql2.core.column.function.Max;
import com.pengwz.dynamic.sql2.entites.SimpleUserEntity;
import com.pengwz.dynamic.sql2.entites.SimpleUserEntity2;
import org.junit.jupiter.api.Test;

import java.util.List;

class SelectorTest {
//    @Test
//    void test() {
//        List<Object> list = Selector.instance()
//                .allColumn()
//                .from(SimpleUserEntity.class)
//                .where(() -> 1 != 1)
//                .toList();
//        System.out.println(list);
//    }
//
//    @Test
//    void test2() {
//        List<Object> list = Selector.instance()
//                .column(SimpleUserEntity::getAge)
//                .from(SimpleUserEntity.class)
//                .toList();
//        System.out.println(list);
//    }

    @Test
    void test3() {
        Selector.instance()
                .column(new Avg(new Max(SimpleUserEntity::getAge))).as("")
                .column(new Avg(SimpleUserEntity::getId))
                .column(SimpleUserEntity::getGenderEnum)
                .column(new Max(SimpleUserEntity::getUsername))
                .column(SimpleUserEntity::getId).as("id1")
                .column(SimpleUserEntity2::getId).as("id2")
                .from(SimpleUserEntity.class).as("t1")
                .join(SimpleUserEntity2.class).as("t2")
                .on(Alias.name("t1").eq(Alias.name("t2")));

    }
}