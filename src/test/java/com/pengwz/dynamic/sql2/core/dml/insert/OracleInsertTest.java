package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.oracle_entities.User;
import org.junit.jupiter.api.Test;

public class OracleInsertTest extends InitializingContext {

    @Test
    void insert() {
        User user = sqlContext.selectByPrimaryKey(User.class, 12);
        user.setName("Hello");
        user.setUserId(null);
        int i = sqlContext.insertSelective(user);
        System.out.println(i);
    }

}
