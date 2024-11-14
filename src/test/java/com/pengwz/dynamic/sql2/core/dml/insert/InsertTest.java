package com.pengwz.dynamic.sql2.core.dml.insert;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import com.pengwz.dynamic.sql2.entites.UserAndOrderView;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

class InsertTest extends InitializingContext {

    @Test
    void insertSelective() {
        Product product = new Product();
        product.setProductName("菠萝手机-insertSelective");
        product.setPrice(6.66);
        product.setStock(666);
        product.setCreatedAt(new Date());
        product.setCategoryId(1);
        int i = sqlContext.insertSelective(product);
        System.out.println(i);
        System.out.println(EntitiesInserter.getLocalEntities());
        System.out.println(product);
    }

    @Test
    void insertSelective2() {
        Product product = new Product();
        product.setProductName("菠萝手机-insertSelective2");
        product.setPrice(6.66);
        product.setStock(666);
        product.setCreatedAt(new Date());
        product.setCategoryId(1);
        int i = sqlContext.insertSelective(product, Arrays.asList(Product::getAttributes, Product::getProductId));
        System.out.println(i);
    }

    @Test
    void insert() {
        Product product = new Product();
        product.setProductName("菠萝手机-insert");
        product.setPrice(6.66);
        product.setStock(666);
        product.setCreatedAt(new Date());
        product.setCategoryId(1);
        int i = sqlContext.insert(product);
        System.out.println(i);
    }

    @Test
    void batchInsert() {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 1; i <= 10_000; i++) {
            Product product = new Product();
            product.setProductName("菠萝手机-insert-1000/" + i);
            product.setPrice(6.66);
            product.setStock(666);
            product.setCreatedAt(new Date());
            product.setCategoryId(1);
            products.add(product);
        }
        long timeMillis = System.currentTimeMillis();
        int i = sqlContext.insertBatch(products);
        System.out.println(System.currentTimeMillis() - timeMillis);
        System.out.println(i);
//        products.forEach(System.out::println);
    }

    @Test
    void insertMultiple() {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 1; i <= 10_000; i++) {
            Product product = new Product();
            product.setProductName("菠萝手机-insertMultiple-1000/" + i);
            product.setPrice(6.66);
            product.setStock(666);
            product.setCreatedAt(new Date());
            product.setCategoryId(1);
            products.add(product);
        }
        long timeMillis = System.currentTimeMillis();
        int i = sqlContext.insertMultiple(products);
        System.out.println(System.currentTimeMillis() - timeMillis);
        System.out.println(i);
//        products.forEach(System.out::println);
    }

    @Test
    void batchInsertMultiple() {
        UserAndOrderView userAndOrderView = new UserAndOrderView();
        userAndOrderView.setPrice(BigDecimal.ONE);
        sqlContext.insertMultiple(Collections.singleton(userAndOrderView));
    }
}