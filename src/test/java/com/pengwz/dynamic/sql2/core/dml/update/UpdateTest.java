package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

class UpdateTest extends InitializingContext {

    @Test
    void updateByPrimaryKey() {
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("新产品");
        sqlContext.updateByPrimaryKey(product);
    }

}