package com.dynamic.sql.mapper;

import com.dynamic.sql.entites.Product;

public interface ProductMapper extends EntityMapper<Product> {

    default void printSayHello() {
        System.out.println("hello world!");
    }
}
