package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.oracle_entities.User;
import org.junit.jupiter.api.Test;

public class OracleInsertTest extends InitializingContext {

    @Test
    void insertSelective() {
//        User user = sqlContext.select()
//                .allColumn()
//                .from(User.class)
//                .where(whereCondition -> whereCondition.andEqualTo(User::getName, "Jerry"))
//                .fetch()
//                .toOne();
        User user = new User();
        user.setName("Hello, Jerry");
        int i = sqlContext.insertSelective(user);
        System.out.println(i);
    }

}
