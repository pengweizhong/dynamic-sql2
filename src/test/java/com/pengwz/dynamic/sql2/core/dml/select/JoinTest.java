package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.Category;
import com.pengwz.dynamic.sql2.entites.Order;
import com.pengwz.dynamic.sql2.entites.Product;
import com.pengwz.dynamic.sql2.entites.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
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

    /**
     * <pre>
     * {@code
     * select
     * 	u.user_id ,
     * 	u.name ,
     * 	o.order_id ,
     * 	o.order_details
     * from
     * 	users u
     * left join orders o
     * on
     * 	u.user_id = o.user_id
     * 	and o.order_date < '2023-12-12'
     * where
     * 	 u.user_id in (1, 2, 3, 4, 5);
     * }
     *     </pre>
     */
    @Test
    void leftJoin() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(User::getUserId)
                .column(User::getName)
                .column(Order::getOrderId)
                .column(Order::getOrderDetails)
                .from(User.class)
                .leftJoin(Order.class,
                        on -> on.orEqualTo(User::getUserId, Order::getUserId)
                                .andLessThan(Order::getOrderDate, LocalDate.of(2023, 12, 12)))
                .where(whereCondition -> whereCondition.andIn(User::getUserId, Arrays.asList(1, 2, 3, 4, 5)))
                .fetchOriginalMap().toList();
        list.forEach(System.out::println);
    }

    /**
     * <pre>
     * {@code
     * select
     * 	u.user_id ,
     * 	u.name ,
     * 	o.order_id ,
     * 	o.order_details
     * from
     * 	users u
     * right join orders o
     * on
     * 	u.user_id = o.user_id
     * 	and o.order_date < '2023-12-12'
     * where
     * 	 u.user_id in (1, 2, 3, 4, 5);
     * }
     *     </pre>
     */
    @Test
    void rightJoin() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(User::getUserId)
                .column(User::getName)
                .column(Order::getOrderId)
                .column(Order::getOrderDetails)
                .from(User.class)
                .rightJoin(Order.class,
                        on -> on.orEqualTo(User::getUserId, Order::getUserId)
                                .andLessThan(Order::getOrderDate, LocalDate.of(2023, 12, 12)))
                .where(whereCondition -> whereCondition.andIn(User::getUserId, Arrays.asList(1, 2, 3, 4, 5)))
                .fetchOriginalMap().toList();
        list.forEach(System.out::println);
    }

    /**
     * <pre>
     * {@code
     * select
     * 	o.*,
     * 	u.*
     * from
     * 	orders o
     * left join users u on
     * 	o.user_id = u.user_id
     * union
     * select
     * 	o.*,
     * 	u.*
     * from
     * 	orders o
     * right join users u on
     * 	o.user_id = u.user_id
     * }
     *     </pre>
     */
    @Test
    void fullJoin() {
        List<Map<String, Object>> list = sqlContext.select()
                .allColumn(User.class)
                .allColumn(Order.class)
                .from(User.class)
                .fullJoin(Order.class,
                        on -> on.orEqualTo(User::getUserId, Order::getUserId))
                .fetchOriginalMap().toList();
        list.forEach(System.out::println);
    }

    /**
     * <pre>
     * {@code
     * select
     * 	o.*,
     * 	u.*
     * from
     * 	orders o
     * cross join users u on
     * 	o.user_id = u.user_id
     * }
     *     </pre>
     */
    @Test
    void crossJoin() {
        List<Map<String, Object>> list = sqlContext.select()
                .allColumn(User.class)
                .allColumn(Order.class)
                .from(User.class)
                .crossJoin(Order.class)
                .fetchOriginalMap().toList();
        list.forEach(System.out::println);
    }

}
