package com.pengwz.dynamic.sql2.core.crud.select;

import com.pengwz.dynamic.sql2.entites.SimpleUserEntity;
import org.junit.jupiter.api.Test;

class SelectorTest {
    @Test
    void test() {

//        Selector.allColumn().from("table").where().toStream();
//        Selector.columnBuilder().column("").build().from("table").where().toStream();

        Selector.instance().allColumn().from(SimpleUserEntity.class).where(() -> 1 != 1).result();
    }

}