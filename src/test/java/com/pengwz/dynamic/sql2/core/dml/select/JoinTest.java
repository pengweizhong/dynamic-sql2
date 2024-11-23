package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Category;
import com.pengwz.dynamic.sql2.entites.Product;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

class JoinTest extends InitializingContext {
    /**
     * <pre>
     * {@code
     * select
     * 	p.product_id ,
     * 	p.product_name ,
     * 	p.created_at ,
     * 	c.category_id ,
     * 	c.category_name ,
     * 	c.description
     * from
     * 	products p
     * join categories c on
     * 	p.category_id = c.category_id
     * where
     * 	p.product_id in(1, 2, 3, 4, 5);
     * </pre>
     */
    @Test
    void join() {
        List<Object> list = sqlContext.select()
                .column(Product::getProductId)
                .column(Product::getProductName)
                .column(Product::getCreatedAt)
                .column(Category::getCategoryId)
                .column(Category::getCategoryName)
                .column(Category::getDescription)
                .from(Product.class)
                .join(Category.class, on -> on.orEqualTo(Product::getCategoryId, Category::getCategoryId))
                .where(whereCondition -> whereCondition.andIn(Product::getProductId, Arrays.asList(1, 2, 3, 4, 5)))
                .fetch(Object.class).toList();
        list.forEach(System.out::println);
    }

    @Test
    void innerJoin() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(Product::getProductId)
                .column(Product::getProductName)
                .column(Product::getCreatedAt)
                .column(Category::getCategoryId)
                .column(Category::getCategoryName)
                .column(Category::getDescription)
                .from(Product.class)
                .innerJoin(Category.class, on -> on.orEqualTo(Product::getCategoryId, Category::getCategoryId))
                .where(whereCondition -> whereCondition.andIn(Product::getProductId, Arrays.asList(1, 2, 3, 4, 5)))
                .fetchOriginalMap().toList();
        list.forEach(System.out::println);
    }
}
