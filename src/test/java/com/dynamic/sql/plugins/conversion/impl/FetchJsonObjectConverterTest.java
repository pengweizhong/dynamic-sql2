package com.dynamic.sql.plugins.conversion.impl;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.Product;
import com.google.gson.JsonObject;
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