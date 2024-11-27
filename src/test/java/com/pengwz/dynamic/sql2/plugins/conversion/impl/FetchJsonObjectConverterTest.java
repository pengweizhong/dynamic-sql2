package com.pengwz.dynamic.sql2.plugins.conversion.impl;

import com.google.gson.JsonObject;
import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

class FetchJsonObjectConverterTest extends InitializingContext {

    @Test
    void test() {
        JsonObject one = sqlContext.select()
                .column(Product::getAttributes)
                .from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 1))
                .fetch(JsonObject.class)
                .toOne();
        System.out.println(one);
    }
}