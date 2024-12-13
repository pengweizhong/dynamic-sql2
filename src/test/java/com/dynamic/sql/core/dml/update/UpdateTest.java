package com.dynamic.sql.core.dml.update;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.entites.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

class UpdateTest extends InitializingContext {

    @Test
    void updateByPrimaryKey() {
        Integer categoryId = sqlContext.select().column(Product::getCategoryId).from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20))
                .fetch(Integer.class).toOne();
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("New Coffee Maker");
        product.setCategoryId(categoryId);
        product.setCreatedAt(new Date());
        product.setPrice(BigDecimal.TEN);
        product.setStock(123);
        int i = sqlContext.updateByPrimaryKey(product);
        System.out.println(i);
    }

    @Test
    void updateSelectiveByPrimaryKey() {
        Integer categoryId = sqlContext.select().column(Product::getCategoryId).from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20))
                .fetch(Integer.class).toOne();
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("New Coffee Maker2");
        product.setCategoryId(categoryId);
        product.setCreatedAt(new Date());
        int i = sqlContext.updateSelectiveByPrimaryKey(product);
        System.out.println(i);
    }

    @Test
    void updateSelectiveByPrimaryKey2() {
        Integer categoryId = sqlContext.select().column(Product::getCategoryId).from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20))
                .fetch(Integer.class).toOne();
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("New Coffee Maker3");
        product.setCategoryId(categoryId);
        product.setCreatedAt(new Date());
        int i = sqlContext.updateSelectiveByPrimaryKey(product, Collections.singletonList(Product::getAttributes));
        System.out.println(i);
    }

    @Test
    void update() {
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("New Coffee Maker4");
        product.setCategoryId(4);
        product.setCreatedAt(new Date());
        product.setPrice(BigDecimal.valueOf(879));
        product.setStock(222);
        int i = sqlContext.update(product, whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20));
        System.out.println(i);
    }

    @Test
    void update2() {
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("New Coffee Maker5");
        product.setCategoryId(4);
        product.setCreatedAt(new Date());
        product.setPrice(BigDecimal.valueOf(879));
        product.setStock(222);
        int i = sqlContext.updateSelective(product, whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20));
        System.out.println(i);
    }

    @Test
    void update3() {
        Product product = new Product();
        product.setProductId(20);
        product.setProductName("New Coffee Maker6");
        product.setCategoryId(4);
        product.setCreatedAt(new Date());
        product.setPrice(BigDecimal.valueOf(879));
        product.setStock(222);
        int i = sqlContext.updateSelective(product, Collections.singletonList(Product::getAttributes), whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20));
        System.out.println(i);
    }

    @Test
    void upsert() {
        Product product = new Product();
//        product.setProductId(20);
        product.setProductName("New Coffee Maker");
        product.setCategoryId(4);
        product.setCreatedAt(new Date());
        product.setPrice(BigDecimal.TEN);
        product.setStock(123);
        int i = sqlContext.upsert(product);
        System.out.println(i);
        System.out.println(product);
    }

    @Test
    void upsertSelective() {
        Product product = sqlContext.select().allColumn().from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 7))
                .fetch().toOne();
        product.setProductName("MacBook Pro2");
        product.setCreatedAt(new Date());
        product.setAttributes("{\"a\":1}");
        product.setProductId(null);
        int i = sqlContext.upsertSelective(product, Collections.singletonList(Product::getAttributes));
        System.out.println(i);
        System.out.println(product);
    }

    @Test
    void upsertSelective2() {
        Product product = sqlContext.select().allColumn().from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 7))
                .fetch().toOne();
        product.setProductName("MacBook Pro2");
        product.setCreatedAt(new Date());
        product.setAttributes(null);
        product.setProductId(null);
        int i = sqlContext.upsertSelective(product);
        System.out.println(i);
        System.out.println(product);
    }

    @Test
    void upsertMultiple() {
        ArrayList<Product> products = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            Product product = new Product();
            product.setProductName("New Coffee Maker " + i);
            product.setCategoryId(4);
            product.setCreatedAt(new Date());
            product.setPrice(BigDecimal.TEN);
            product.setStock(123);
            products.add(product);
        }
        int i = sqlContext.upsertMultiple(products);
        System.out.println(i);
        products.forEach(System.out::println);
    }

}