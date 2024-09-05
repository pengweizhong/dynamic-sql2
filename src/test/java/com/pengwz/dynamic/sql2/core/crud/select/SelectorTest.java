package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.core.TableAlias;
import com.pengwz.dynamic.sql2.core.column.function.Avg;
import com.pengwz.dynamic.sql2.core.column.function.Max;
import com.pengwz.dynamic.sql2.core.column.function.Md5;
import com.pengwz.dynamic.sql2.core.column.function.Upper;
import com.pengwz.dynamic.sql2.entites.SimpleUserEntity;
import com.pengwz.dynamic.sql2.entites.SimpleUserEntity2;
import org.junit.jupiter.api.Test;

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
                .column(SimpleUserEntity::getGenderEnum)
                .column(new Avg(new Max(SimpleUserEntity::getAge))).as("")
                .column(new Avg(SimpleUserEntity::getId))
                .column(new Upper(new Md5(SimpleUserEntity::getUsername)))
                .column(SimpleUserEntity::getId).as("id1")
                .column(SimpleUserEntity2::getId).as("id2")
                .from(SimpleUserEntity.class).as("t1")
                .join(SimpleUserEntity2.class).as("t2")
                .on(TableAlias.name("t1").column(SimpleUserEntity::getId).eq(TableAlias.name("t2").column(SimpleUserEntity2::getId)))
                .and(TableAlias.name("t1").column(SimpleUserEntity::getId).eq(TableAlias.name("t2").column(SimpleUserEntity2::getId)))
        ;
    }
}