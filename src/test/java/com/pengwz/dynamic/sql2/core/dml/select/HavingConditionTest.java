package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Max;
import com.pengwz.dynamic.sql2.entites.User;
import org.junit.jupiter.api.Test;

class HavingConditionTest extends InitializingContext {

    @Test
    void andEqualTo() {
        User one = sqlContext.select().allColumn()
                .from(User.class)
                .groupBy(User::getUserId)
                .having(havingCondition -> havingCondition.andEqualTo(new Max(User::getUserId), 5))
                .fetch()
                .toOne();
        System.out.println(one);
    }
}