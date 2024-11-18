package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class DeleteTest extends InitializingContext {

    @Test
    void deleteByPrimaryKey() {
        int pkValue = 5011;
        int i = sqlContext.deleteByPrimaryKey(Product.class, pkValue);
        System.out.println(i);
    }

    @Test
    void deleteByPrimaryKey2() {
        int i = sqlContext.deleteByPrimaryKey(Product.class, Arrays.asList(5001, 5002, 5003, 5004, 5005));
        System.out.println(i);
    }

    @Test
    void delete() {
        int i = sqlContext.delete(Product.class, where -> {
            where.andEqualTo(Product::getProductId, 5006);
            where.orEqualTo(Product::getProductId, 5007);
        });
        System.out.println(i);
    }

}