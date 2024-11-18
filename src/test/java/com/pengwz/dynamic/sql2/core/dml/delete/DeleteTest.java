package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

class DeleteTest extends InitializingContext {

    @Test
    void delete() {
        int pkValue = 5011;
        int i = sqlContext.deleteByPrimaryKey(Product.class, pkValue);
        System.out.println(i);
    }

}