package com.pengwz.dynamic.sql2.mapper;

import com.pengwz.dynamic.sql2.entites.Product;

public interface ProductMapper extends EntityMapper<Product> {

    default void printSayHello() {
        System.out.println("hello world!");
    }
}
