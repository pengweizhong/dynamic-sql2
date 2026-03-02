package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.anno.Table;
import lombok.Data;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.List;

public class TestTemp extends InitializingContext {
    @Test
    void test() {
        TestBigintEntity testBigintEntity = new TestBigintEntity();
        testBigintEntity.setName("hahaha");
        sqlContext.insertSelective(testBigintEntity);
    }

    @Test
    void test2() {
        List<TestBigintEntity> list = sqlContext.select().allColumn().from(TestBigintEntity.class).fetch().toList();
        System.out.println(list);
    }
}

@Data
@Table("t_test_bigint")
class TestBigintEntity {
    private BigInteger id;
    private String name;
}