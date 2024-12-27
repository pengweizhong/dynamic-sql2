package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.core.column.function.windows.aggregate.Max;
import com.dynamic.sql.entites.User;
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