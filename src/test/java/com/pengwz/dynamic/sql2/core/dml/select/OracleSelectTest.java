package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.oracle_entities.User;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OracleSelectTest extends InitializingContext {

    @Test
    void testSelect() {
        List<User> list = sqlContext.select().allColumn().from(User.class).fetch().toList();
        list.forEach(System.out::println);
    }
}
