package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.entites.Product;
import com.pengwz.dynamic.sql2.mapper.EntityMapper;
import com.pengwz.dynamic.sql2.mapper.MapperFactory;
import com.pengwz.dynamic.sql2.mapper.ProductMapper;
import org.junit.jupiter.api.Test;

public class MapperSelect {

    @Test
    void testMapper() {
        EntityMapper<Product> mapper = MapperFactory.createMapper(ProductMapper.class);
        Product product = mapper.selectByPrimaryKey(7);
        System.out.println(product);
    }
}
