package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.entites.User;
import org.junit.jupiter.api.Test;

import static com.pengwz.dynamic.sql2.core.column.conventional.AbstractAlias.withTableAlias;

public class SelectTest extends InitializingContext {
    /**
     * 从多个表中提取用户及其订单相关的信息，包括用户的总花费、订单数量、所购买的产品及其分类等
     * <p/>
     * <pre>
     * {@code
     * SELECT
     *     u.user_id,
     *     u.name AS user_name,
     *     user_total.total_spent,
     *     user_total.total_orders,
     *     p.product_name,
     *     p.price,
     *     cat.category_name,
     *     p.stock
     * FROM users u
     * -- 子查询：计算每个用户的总花费和订单数量
     * JOIN (
     *     SELECT
     *         o.user_id,
     *         SUM(o.total_amount) AS total_spent,
     *         COUNT(o.order_id) AS total_orders
     *     FROM orders o
     *     GROUP BY o.user_id
     * ) AS user_total ON u.user_id = user_total.user_id  -- 关联用户和子查询结果
     * LEFT JOIN orders o ON u.user_id = o.user_id  -- 左连接订单
     * LEFT JOIN (
     *     SELECT
     *         p.product_id,
     *         p.product_name,
     *         p.price,
     *         p.category_id,
     *         p.stock,  -- 包含 stock 字段
     *         jt.order_id
     *     FROM products p
     *     JOIN (
     *         SELECT
     *             o.order_id,
     *             jt.product_name
     *         FROM orders o
     *         JOIN JSON_TABLE(o.order_details, '$.items[*]'
     *             COLUMNS (product_name VARCHAR(150) PATH '$.product')) AS jt
     *     ) AS jt ON jt.product_name = p.product_name
     * ) AS p ON o.order_id = p.order_id  -- 连接产品
     * LEFT JOIN categories cat ON p.category_id = cat.category_id  -- 关联产品和分类
     * WHERE user_total.total_spent > 100  -- 只选择花费超过 100 的用户
     * ORDER BY user_total.total_spent DESC  -- 按照总花费降序排列
     * LIMIT 0, 500;  -- 限制返回结果的行数
     * }
     * </pre>
     */
    @Test
    void select1() {
//        sqlContext.select().allColumn("user").from(User.class,"user").fetch().toList();
//
//        sqlContext.select().allColumn(User.class).from(User.class).fetch().toList();
//
//        sqlContext.select().allColumn().from(User.class).join(Product.class,"p",condition -> condition.andEqualTo(User::getName,Product::getProductId)).fetch().toList();

        sqlContext.select()
                .allColumn("s")
                .allColumn(User.class)
                .from(select -> select.column(User::getName).column(User::getEmail).from(User.class), "s")
                .join(User.class, on -> on.andEqualTo(withTableAlias("s", User::getName), User::getName))
                .fetch().toList();

//        sqlContext.select().column("s", User::getName).column("s", User::getEmail)
//                .from(select -> select.column(User::getName).column(User::getEmail).from(User.class), "s").fetch().toList();
    }
}




























