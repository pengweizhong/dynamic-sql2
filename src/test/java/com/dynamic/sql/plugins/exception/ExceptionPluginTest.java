package com.dynamic.sql.plugins.exception;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.User;
import com.dynamic.sql.plugins.pagination.PageHelper;
import com.dynamic.sql.plugins.pagination.PageInfo;
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

    @Test
    void test4() {
        sqlContext.execute("insert into `users` (`user_id`,name,registration_date) values (111111111111111111111111111111111,'name','2020-01-01')");
    }

    @Test
    void test5() {
        PageInfo<Object> objectPageInfo = PageHelper.of(1, 10).selectPage(
                () -> sqlContext.execute("select * from  `users`  where user_id=99999999999999999fhkjdsf使得粉红色地方")
        );
        System.out.println(objectPageInfo);
    }

    @Test
    void test6() {
        PageInfo<Object> objectPageInfo = PageHelper.of(1, 10).selectPage(
                () -> sqlContext.execute("select * from  `users`  ")
        );
        System.out.println(objectPageInfo);
    }
}