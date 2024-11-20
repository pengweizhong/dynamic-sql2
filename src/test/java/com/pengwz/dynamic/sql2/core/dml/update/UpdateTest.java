package com.pengwz.dynamic.sql2.core.dml.update;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
        Product product = sqlContext.select().allColumn().from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 20))
                .fetch().toOne();
        product.setProductName("New Coffee Maker -> upsert");
        product.setCreatedAt(new Date());
        int i = sqlContext.upsert(product);
        System.out.println(i);
    }

}