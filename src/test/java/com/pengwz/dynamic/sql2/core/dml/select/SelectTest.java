package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.InitializingContext;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Count;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.Sum;
import com.pengwz.dynamic.sql2.core.column.function.table.JsonTable;
import com.pengwz.dynamic.sql2.core.column.function.table.JsonTable.JsonColumn;
import com.pengwz.dynamic.sql2.entites.Category;
import com.pengwz.dynamic.sql2.entites.Order;
import com.pengwz.dynamic.sql2.entites.Product;
import com.pengwz.dynamic.sql2.entites.User;
import com.pengwz.dynamic.sql2.enums.SortOrder;
import org.junit.jupiter.api.Test;

import static com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper.withOriginColumn;
import static com.pengwz.dynamic.sql2.core.column.AbstractAliasHelper.withTableAlias;

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
        sqlContext.select()
                .column("u", User::getUserId)
                .column("u", User::getName, "user_name")
                .column(withOriginColumn("user_total.total_spent"))
                .column(withOriginColumn("user_total.total_orders"))
                .column("p", Product::getProductName)
                .column("p", Product::getPrice)
                .column("cat", Category::getCategoryName)
                .column("p", Product::getStock)
                .from(User.class, "u")
                .join(select -> select
                                .column("o", Order::getUserId)
                                .column(new Sum(withOriginColumn("o.total_amount")), "total_spent")
                                .column(new Count(withOriginColumn("o.order_id")), "total_orders")
                                .from(Order.class, "o")
                                .groupBy(withTableAlias("o", Order::getUserId))
                        , "user_total",
                        condition -> condition.andEqualTo(withTableAlias("u", User::getUserId)
                                , withTableAlias("user_total", Order::getUserId))
                )
                .leftJoin(Order.class, "o", condition -> condition.andEqualTo(withTableAlias("u", User::getUserId),
                        withTableAlias("o", Order::getUserId)))
                .leftJoin(select -> select
                        .column("p", Product::getProductId)
                        .column("p", Product::getProductName)
                        .column("p", Product::getPrice)
                        .column("p", Product::getCategoryId)
                        .column("p", Product::getStock)
                        .column("jt", Order::getOrderId)
                        .from(Product.class, "p")
                        .join(select1 -> select1
                                        .column("o", Order::getOrderId)
                                        .column("jt", Product::getProductName)
                                        .from(Order.class, "o")
                                        .join(() -> new JsonTable(withOriginColumn("o.order_details"), "$.items[*]",
                                                JsonColumn.builder().column("product_name").dataType("VARCHAR(150)").jsonPath("$.product").build()
                                        ), "jt", null), "jt",
                                condition -> condition.andEqualTo(withTableAlias("jt", Product::getProductName),
                                        withTableAlias("p", Product::getProductName))), "p", condition -> condition.andEqualTo(withTableAlias("o", Order::getOrderId), withTableAlias("p", Order::getOrderId)))
                .leftJoin(Category.class, "cat", condition -> condition.andEqualTo(withTableAlias("p", Category::getCategoryId),
                        withTableAlias("cat", Category::getCategoryId)))
                .where(condition -> condition.andGreaterThan(withOriginColumn("user_total.total_spent"), 100))
                .orderBy(withOriginColumn("user_total.total_spent"), SortOrder.DESC)
                .limit(0, 500)
                .fetch().toList();
    }

    /**
     * <pre>
     * {@code
     * SELECT
     * 	o. order_id,
     * 	jt. product_name
     * FROM
     * 	orders o
     * JOIN JSON_TABLE(o. order_details,
     * 	'$.items[*]' COLUMNS (product_name VARCHAR(150) PATH '$.product')) AS jt
     * }
     * </>
     */
    @Test
    public void test1() {
        sqlContext.select()
                .column("o", Order::getOrderId)
                .column("jt", Product::getProductName)
                .from(Order.class, "o")
                .join(() -> new JsonTable(withTableAlias("o", Order::getOrderDetails), "$.items[*]",
                        JsonColumn.builder().column("product_name").dataType("VARCHAR(150)").jsonPath("$.product").build()
                ), "jt", null)
                .fetch().toList();
    }
}




























