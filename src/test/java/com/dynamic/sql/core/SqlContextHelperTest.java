package com.dynamic.sql.core;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.context.SqlContextHelper;
import com.dynamic.sql.entites.User;
import org.junit.jupiter.api.Test;

import java.util.List;

class SqlContextHelperTest extends InitializingContext {

    List<User> selectUsers() {
        return sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> whereCondition.andLessThanOrEqualTo(User::getUserId, 3))
                .fetch().toList();
    }

    @Test
    void suppressSqlLog() {
        List<User> users = SqlContextHelper.suppressSqlLog(this::selectUsers);
        System.out.println(users);
    }

}