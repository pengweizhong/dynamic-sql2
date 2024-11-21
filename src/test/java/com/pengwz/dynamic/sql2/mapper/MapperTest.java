package com.pengwz.dynamic.sql2.mapper;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Product;
import com.pengwz.dynamic.sql2.plugins.pagination.PageHelper;
import com.pengwz.dynamic.sql2.plugins.pagination.PageInfo;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;

class MapperTest extends InitializingContext {
    static EntityMapper<Product> productEntityMapper;

    @BeforeAll
    static void setUp() {
        productEntityMapper = MapperProxyFactory.loadMapper(ProductMapper.class);
    }

    @Test
    void select() {
        PageInfo<Map<Integer, String>> pageInfo = PageHelper.of(1, 3).selectPage(() -> productEntityMapper.select()
                .column(Product::getProductId)
                .column(Product::getProductName)
                .from(Product.class)
                .fetch().toMap(Product::getProductId, Product::getProductName));
        long total = pageInfo.getTotal();
        System.out.println("total -> " + total);
        pageInfo.getRecords().forEach((k, v) -> System.out.println("key->" + k + ", value->" + v));
    }

    @Test
    void selectByPrimaryKey() {
        Product product = productEntityMapper.selectByPrimaryKey(7);
        System.out.println(product);
    }

    @Test
    void insertSelective() {
        Product product = new Product();
        product.setStock(1);
        product.setProductName("test");
        product.setPrice(BigDecimal.ONE);
        product.setCreatedAt(new Date());
        product.setCategoryId(4);
        productEntityMapper.insertSelective(product);
        System.out.println(product);
    }

    @Test
    void insertSelective2() {
        Product product = new Product();
        product.setStock(1);
        product.setProductName("test2");
        product.setPrice(BigDecimal.ONE);
        product.setCreatedAt(new Date());
        product.setCategoryId(4);
        productEntityMapper.insertSelective(product, Collections.singleton(Product::getAttributes));
        System.out.println(product);
    }

    @Test
    void deleteByPrimaryKey() {
        int i = productEntityMapper.deleteByPrimaryKey(5097);
        System.out.println(i);
    }

}
