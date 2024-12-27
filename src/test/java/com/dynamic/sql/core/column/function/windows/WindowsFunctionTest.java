package com.dynamic.sql.core.column.function.windows;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.core.column.function.windows.aggregate.Sum;
import com.dynamic.sql.entites.Order;
import com.dynamic.sql.entites.User;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

class WindowsFunctionTest extends InitializingContext {

    @Test
    void rowNumber() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(new RowNumber(), over -> over.orderBy("aaa", "sumTotalAmount"), "rowNum")
                .allColumn()
                .from(
                        select -> select.column(Order::getUserId, "userId")
                                .column(new Sum(Order::getTotalAmount), "sumTotalAmount")
                                .from(Order.class)
                                .groupBy(Order::getUserId), "aaa")
                .fetchOriginalMap()
                .toList();
        list.forEach(System.out::println);
    }

//    @Test
//    void rowNumber2() {
//        List<Map<String, Object>> list = sqlContext.select()
//                .column(new RowNumber(), over -> over.orderBy(Order::getTotalAmount), "rowNum")
//                .allColumn()
//                .from(
//                        select -> select.column(Order::getUserId, "userId")
//                                .column(new Sum(Order::getTotalAmount))
//                                .from(Order.class)
//                                .groupBy(Order::getUserId), "aaa")
//                .fetchOriginalMap()
//                .toList();
//        list.forEach(System.out::println);
//    }

    @Test
    void select() {
        List<Map<String, Object>> list = sqlContext.select()
                .allColumn(Order.class)
                .from(Order.class, "t")
                .join(User.class, on -> on.andEqualTo(Order::getUserId, User::getUserId))
                .where()
                .fetchOriginalMap()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void rank() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(new Rank(), over -> over.orderBy("aaa", "sumTotalAmount"), "rank1")
                .allColumn()
                .from(
                        select -> select.column(Order::getUserId, "userId")
                                .column(new Sum(Order::getTotalAmount), "sumTotalAmount")
                                .from(Order.class)
                                .groupBy(Order::getUserId), "aaa")
                .fetchOriginalMap()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void rank2() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(new RowNumber(), over -> over.orderBy("aaa", "sumTotalAmount"), "rowNum")
                .column(new Rank(), over -> over.orderBy("aaa", "sumTotalAmount"), "rank1")
                .allColumn()
                .from(
                        select -> select.column(Order::getUserId, "userId")
                                .column(new Sum(Order::getTotalAmount), "sumTotalAmount")
                                .from(Order.class)
                                .groupBy(Order::getUserId), "aaa")
                .fetchOriginalMap()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void denseRank() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(new DenseRank(), over -> over.orderBy("aaa", "sumTotalAmount"), "DenseRank")
                .allColumn()
                .from(
                        select -> select.column(Order::getUserId, "userId")
                                .column(new Sum(Order::getTotalAmount), "sumTotalAmount")
                                .from(Order.class)
                                .groupBy(Order::getUserId), "aaa")
                .fetchOriginalMap()
                .toList();
        list.forEach(System.out::println);
    }

    @Test
    void denseRank2() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(new DenseRank(), over -> over.orderBy("aaa", "sumTotalAmount"), "DenseRank")
                .column(new RowNumber(), over -> over.orderBy("aaa", "sumTotalAmount"), "rowNum")
                .column(new Rank(), over -> over.orderBy("aaa", "sumTotalAmount"), "rank1")
                .allColumn()
                .from(
                        select -> select.column(Order::getUserId, "userId")
                                .column(new Sum(Order::getTotalAmount), "sumTotalAmount")
                                .from(Order.class)
                                .groupBy(Order::getUserId), "aaa")
                .fetchOriginalMap()
                .toList();
        list.forEach(System.out::println);
    }

}