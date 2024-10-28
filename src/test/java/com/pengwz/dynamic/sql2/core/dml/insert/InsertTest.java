package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

class InsertTest extends InitializingContext {

    @Test
    void insertSelective() {
        Product product = new Product();
        product.setProductName("菠萝手机");
        product.setPrice(6.66);
        product.setStock(666);
        product.setCreatedAt(new Date());
        product.setCategoryId(1);
        int i = sqlContext.insertSelective(product);
        System.out.println(i);
    }

    @Test
    void insertSelective2() {
        Product product = new Product();
        product.setProductName("菠萝手机");
        product.setPrice(6.66);
        product.setStock(666);
        product.setCreatedAt(new Date());
        product.setCategoryId(1);
        int i = sqlContext.insertSelective(product, Arrays.asList(Product::getAttributes, Product::getProductId));
        System.out.println(i);
    }
}