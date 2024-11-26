package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Length;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Md5;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.SubString;
import com.pengwz.dynamic.sql2.core.column.function.scalar.string.Upper;
import com.pengwz.dynamic.sql2.entites.Product;
import com.pengwz.dynamic.sql2.entites.User;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
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
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> {
                            whereCondition.andEqualTo(User::getName, new SubString("Dana Lee666", 1, 8));
                            whereCondition.orEqualTo("99914B932BD37A50B983C5E7C90AE93B", new Upper(new Md5("{}")));
                        }
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }


    @Test
    void andNotEqualTo() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> whereCondition.andNotEqualTo(User::getName, new SubString("Jerry123", 1, 5)))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andNotEqualTo2() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> whereCondition.andNotEqualTo(User::getName, User::getEmail))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andLengthEquals() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andEqualTo(5, new Length(User::getName)))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }


    @Test
    void andIsNull() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andIsNull(User::getName).orIsNull(User::getName))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andIsNotNull() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andIsNotNull(User::getName).orIsNotNull(User::getName))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andGreaterThan() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andGreaterThan(User::getUserId, 5))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andGreaterThan2() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andGreaterThan(User::getUserId, User::getUserId))
                .fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test()
    void andIn() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andIn(User::getUserId, Arrays.asList(1, 2, 3))
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }


    @Test
    void andBetween() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andBetween(User::getUserId, 1, 2)
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }


    @Test
    void andLike() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andLike(User::getName, "%rr%")
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andMatches() {
        //匹配以 "J" 开头
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andMatches(User::getName, "^J[a-zA-Z]")
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andMatches2() {
        SchemaProperties dataSource = SchemaContextHolder.getSchemaProperties("dataSource");
        dataSource.setDatabaseProductVersion("5.7");
        //匹配以 "J" 开头
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition ->
                        whereCondition.andMatches(User::getName, "^J[a-zA-Z]")
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
        dataSource.setDatabaseProductVersion("8.1.0");
    }

    @Test
    void andFindInSet() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .where(whereCondition ->
                        whereCondition.andFindInSet(Product::getProductName, "14")
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void andFindInSet2() {
        List<Map<String, Object>> mapList = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .where(whereCondition ->
                        whereCondition.andFindInSet(Product::getProductName, "14", " ")
                ).fetchOriginalMap()
                .toList();
        mapList.forEach(System.out::println);
    }

    @Test
    void limit() {
        Map<String, Object> one = sqlContext.select()
                .allColumn()
                .from(Product.class).where(whereCondition -> whereCondition.limit(1)).fetchOriginalMap().toOne();
        System.out.println(one);
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