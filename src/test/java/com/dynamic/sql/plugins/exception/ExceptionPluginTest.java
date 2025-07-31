package com.dynamic.sql.plugins.exception;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.User;
import org.junit.jupiter.api.Test;

class ExceptionPluginTest extends InitializingContext {

    @Test
    void test() {
        User user = new User();
        int i = sqlContext.insertSelective(user);
        System.out.println(i);
    }

    @Test
    void test1() {
        sqlContext.execute("insert into `users` (`user_id`) values (?)");
    }

    @Test
    void test2() {
        sqlContext.execute("insert into `users` (`user_id`) values ()");
    }

    @Test
    void test3() {
        sqlContext.execute("insert into `users` (`user_id`,name,registration_date) values (1,'name','2020-01-01')");
    }
}