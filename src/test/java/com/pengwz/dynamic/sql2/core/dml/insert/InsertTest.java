package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

import java.util.Date;

class InsertTest extends InitializingContext {

    @Test
    void insertSelective() {
        Product product = new Product();
        product.setProductName("菠萝手机");
        product.setPrice(6.66);
        product.setStock(666);
        product.setCreatedAt(new Date());
//        int i = sqlContext.insertSelective(product);
//        System.out.println(i);
    }
}