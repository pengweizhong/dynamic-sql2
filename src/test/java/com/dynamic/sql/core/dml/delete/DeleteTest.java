package com.dynamic.sql.core.dml.delete;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.Product;
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
            where.andEqualTo(Product::getProductId, 1);
            where.orCondition(nestedWhere -> {
                nestedWhere.andEqualTo(Product::getProductId, 3);
                nestedWhere.orEqualTo(Product::getProductId, 4);
            });
        });
        System.out.println(i);
    }

    @Test
    void deleteAll() {
        int i = sqlContext.delete(Product.class, null);
        System.out.println(i);
    }

}