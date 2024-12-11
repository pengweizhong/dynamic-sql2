package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.context.SchemaContextHolder;
import com.pengwz.dynamic.sql2.context.properties.SchemaProperties;
import com.pengwz.dynamic.sql2.enums.DbType;
import com.pengwz.dynamic.sql2.oracle_entities.User;
import com.pengwz.dynamic.sql2.plugins.pagination.PageHelper;
import com.pengwz.dynamic.sql2.plugins.pagination.PageInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

public class OracleSelectTest extends InitializingContext {

    @Test
    void testSelect() {
        List<User> list = sqlContext.select().allColumn().from(User.class).fetch().toList();
        list.forEach(System.out::println);
    }

    @Test
    void testSelectPage() {
        PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
                .selectPage(() -> sqlContext.select()
                        .allColumn()
                        .from(User.class)
                        .orderBy(User::getUserId)
                        .fetch().toList());
        pageInfo.getRecords().forEach(System.out::println);
    }

    @Test
    void testSelectOldPage() {
        SchemaProperties schemaProperties = SchemaContextHolder.getSchemaProperties("oracleDataSource");
        schemaProperties.setDatabaseProductVersion(DbType.ORACLE, "11.0.0.1");
        PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
                .selectPage(() -> sqlContext.select()
                        .allColumn()
                        .from(User.class)
                        .orderBy(User::getUserId)
                        .fetch().toList());
        pageInfo.getRecords().forEach(System.out::println);
    }

    @Test
    void testSelect2() {
        List<User> jerry = sqlContext.select()
                .allColumn()
                .from(User.class)
                .where(whereCondition -> {
                    whereCondition.andEqualTo(User::getName, "Jerry");
                    whereCondition.andEqualTo(User::getRegistrationDate, LocalDate.of(2024,2,1));
                    whereCondition.andLessThan(User::getAccountBalance, 2022);
                })
                .fetch()
                .toList();
        jerry.forEach(System.out::println);
    }

}