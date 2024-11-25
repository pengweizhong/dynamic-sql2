package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.SubString;
import com.pengwz.dynamic.sql2.entites.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class ConditionTest extends InitializingContext {

    @Test
    void andEqualTo() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> whereCondition.andEqualTo(User::getName, new SubString("Jerry123", 1, 5)))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void testAndEqualTo() {
    }

    @Test
    void testAndEqualTo1() {
    }

    @Test
    void orEqualTo() {
    }

    @Test
    void testOrEqualTo() {
    }

    @Test
    void andNotEqualTo() {
    }

    @Test
    void testAndNotEqualTo() {
    }

    @Test
    void orNotEqualTo() {
    }

    @Test
    void testOrNotEqualTo() {
    }

    @Test
    void andLengthEquals() {
    }

    @Test
    void orLengthEquals() {
    }

    @Test
    void andLengthGreaterThan() {
    }

    @Test
    void orLengthGreaterThan() {
    }

    @Test
    void andLengthLessThan() {
    }

    @Test
    void orLengthLessThan() {
    }

    @Test
    void andIsEmpty() {
    }

    @Test
    void orIsEmpty() {
    }

    @Test
    void andIsNotEmpty() {
    }

    @Test
    void orIsNotEmpty() {
    }

    @Test
    void andIsNull() {
    }

    @Test
    void orIsNull() {
    }

    @Test
    void andIsNotNull() {
    }

    @Test
    void orIsNotNull() {
    }

    @Test
    void andGreaterThan() {
    }

    @Test
    void testAndGreaterThan() {
    }

    @Test
    void orGreaterThan() {
    }

    @Test
    void testOrGreaterThan() {
    }

    @Test
    void andGreaterThanOrEqualTo() {
    }

    @Test
    void testAndGreaterThanOrEqualTo() {
    }

    @Test
    void orGreaterThanOrEqualTo() {
    }

    @Test
    void testOrGreaterThanOrEqualTo() {
    }

    @Test
    void andLessThan() {
    }

    @Test
    void testAndLessThan() {
    }

    @Test
    void orLessThan() {
    }

    @Test
    void testOrLessThan() {
    }

    @Test
    void andLessThanOrEqualTo() {
    }

    @Test
    void testAndLessThanOrEqualTo() {
    }

    @Test
    void orLessThanOrEqualTo() {
    }

    @Test
    void testOrLessThanOrEqualTo() {
    }

    @Test
    void andIn() {
    }

    @Test
    void orIn() {
    }

    @Test
    void andNotIn() {
    }

    @Test
    void orNotIn() {
    }

    @Test
    void andBetween() {
    }

    @Test
    void testAndBetween() {
    }

    @Test
    void orBetween() {
    }

    @Test
    void testOrBetween() {
    }

    @Test
    void andNotBetween() {
    }

    @Test
    void orNotBetween() {
    }

    @Test
    void andLike() {
    }

    @Test
    void orLike() {
    }

    @Test
    void andNotLike() {
    }

    @Test
    void orNotLike() {
    }

    @Test
    void andMatches() {
    }

    @Test
    void orMatches() {
    }

    @Test
    void andFindInSet() {
    }

    @Test
    void testAndFindInSet() {
    }

    @Test
    void orFindInSet() {
    }

    @Test
    void testOrFindInSet() {
    }

    @Test
    void andContains() {
    }

    @Test
    void orContains() {
    }

    @Test
    void andAnyIn() {
    }

    @Test
    void orAnyIn() {
    }

    @Test
    void andAllIn() {
    }

    @Test
    void orAllIn() {
    }

    @Test
    void andIsPositive() {
    }

    @Test
    void orIsPositive() {
    }

    @Test
    void andIsNegative() {
    }

    @Test
    void orIsNegative() {
    }

    @Test
    void limit() {
    }

    @Test
    void testLimit() {
    }

    @Test
    void andCondition() {
    }

    @Test
    void orCondition() {
    }

    @Test
    void customCondition() {
    }
}