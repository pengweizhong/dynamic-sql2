package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.oracle_entities.User;
import com.pengwz.dynamic.sql2.plugins.pagination.PageHelper;
import com.pengwz.dynamic.sql2.plugins.pagination.PageInfo;
import org.junit.jupiter.api.Test;

import java.util.List;

public class OracleSelectTest extends InitializingContext {

    @Test
    void testSelect() {
        List<User> list = sqlContext.select().allColumn().from(User.class).fetch().toList();
        list.forEach(System.out::println);
    }

    //现代版本（12c 及以上）：
    //	•	优先使用 OFFSET 和 FETCH，语法简洁、性能好。
    //	•	对复杂排序需求，可结合 ROW_NUMBER()。
    //旧版本（11g 及以下）：
    //	•	使用 ROWNUM 实现分页，注意其局限性。
    @Test
    void testSelectPage() {
        PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
                .selectPage(() -> sqlContext.select().allColumn().from(User.class).fetch().toList());
        pageInfo.getRecords().forEach(System.out::println);
    }

}
