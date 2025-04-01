package com.dynamic.sql.core.dml.select;

import com.dynamic.sql.HelloTest;
import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.ColumnReference;
import com.dynamic.sql.core.column.conventional.NumberColumn;
import com.dynamic.sql.core.column.function.modifiers.Distinct;
import com.dynamic.sql.core.column.function.scalar.datetime.DateFormat;
import com.dynamic.sql.core.column.function.scalar.datetime.Now;
import com.dynamic.sql.core.column.function.scalar.number.Round;
import com.dynamic.sql.core.column.function.table.JsonTable;
import com.dynamic.sql.core.column.function.table.JsonTable.JsonColumn;
import com.dynamic.sql.core.column.function.windows.DenseRank;
import com.dynamic.sql.core.column.function.windows.aggregate.Count;
import com.dynamic.sql.core.column.function.windows.aggregate.Sum;
import com.dynamic.sql.core.placeholder.ParameterBinder;
import com.dynamic.sql.entites.*;
import com.dynamic.sql.enums.SortOrder;
import com.dynamic.sql.model.Dual;
import com.dynamic.sql.plugins.pagination.CollectionPage;
import com.dynamic.sql.plugins.pagination.MapPage;
import com.dynamic.sql.plugins.pagination.PageHelper;
import com.dynamic.sql.plugins.pagination.PageInfo;
import com.dynamic.sql.utils.SqlUtils;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.dynamic.sql.core.column.AbstractAliasHelper.bindAlias;
import static com.dynamic.sql.core.column.AbstractAliasHelper.bindName;

public class SelectTest extends InitializingContext {
    private static final Logger log = LoggerFactory.getLogger(SelectTest.class);

    @Test
    void testLog() {
        log.trace("这是trace日志");
        log.debug("这是debug日志");
        log.info("这是info日志");
        log.warn("这是warn日志");
        log.error("这是error日志");
    }

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
        List<UserAndOrderView> list = sqlContext.select()
                .column(User::getUserId)
                .column(User::getName, "user_name")
                .column("user_total", "total_spent")
                .column("user_total", "total_orders")
                .column("p", Product::getProductName)
                .column("p", Product::getPrice)
                .column(Category::getCategoryName)
                .column("p", Product::getStock)
                .from(User.class)
                .join(select -> select
                                .column(Order::getUserId)
                                .column(new Sum(Order::getTotalAmount), "total_spent")
                                .column(new Count(Order::getOrderId), "total_orders")
                                .from(Order.class)
                                .groupBy(Order::getUserId)
                        , "user_total",
                        condition -> condition.andEqualTo(User::getUserId, bindAlias("user_total", Order::getUserId))
                )
                .leftJoin(Order.class, condition -> condition.andEqualTo(User::getUserId, Order::getUserId))
                .leftJoin(select -> select
                        .column(Product::getProductId)
                        .column(Product::getProductName)
                        .column(Product::getPrice)
                        .column(Product::getCategoryId)
                        .column(Product::getStock)
                        .column("jt", Order::getOrderId)
                        .from(Product.class)
                        .join(select1 -> select1
                                        .column("o", Order::getOrderId)
                                        .column("jt", Product::getProductName)
                                        .from(Order.class, "o")
                                        .join(() -> new JsonTable("o", "order_details", "$.items[*]",
                                                JsonColumn.builder().column("product_name").dataType("VARCHAR(150)").jsonPath("$.product").build()
                                        ), "jt"),
                                "jt", condition -> condition.andEqualTo(bindAlias("jt", Product::getProductName), Product::getProductName)
                        ), "p", condition -> condition.andEqualTo(Order::getOrderId, bindAlias("p", Order::getOrderId)))
                .leftJoin(Category.class, condition -> condition.andEqualTo(bindAlias("p", Category::getCategoryId), Category::getCategoryId))
                .where(condition -> condition.andGreaterThan(bindAlias("user_total", "total_spent"), 100))
                .orderBy("user_total", "total_spent", SortOrder.DESC)
                .limit(0, 100)
                .fetch(UserAndOrderView.class).toList();
        System.out.println(list);
    }

    @Test
    void select1_1() {
        List<UserAndOrderView> list = sqlContext.select()
                .column(User::getUserId)
                .column(User::getName, "user_name")
                .from(User.class)
                .where(whereCondition -> {
                    whereCondition.andExists(select -> select
                            .column(new NumberColumn(1))
                            .from(() -> new JsonTable(User::getName, "$.items[*]",
                                    JsonTable.JsonColumn.builder().column("value").dataType("VARCHAR(50)").jsonPath("$").build()
                            ), "jt"));
                }).fetch(UserAndOrderView.class).toList();
        System.out.println(list);
    }

    @Test
    void select2() {
        Integer count = sqlContext.select().allColumn()
                .from(select -> select.column(new Count(Product::getProductName)).from(Product.class), "ss")
                .fetch(Integer.class).toOne();
        System.out.println(count);
    }

    @Test
    void select3() {
        Supplier<Set<ProductView>> selectSupplier = () -> sqlContext.select()
                .column(Product::getProductId)
                .from(Product.class)
                .fetch(ProductView.class).toSet();
        CollectionPage<Set<ProductView>, ProductView> collectionPage = PageHelper.ofCollection(1, 3).selectPage(selectSupplier);
        System.out.println(collectionPage);
        while (collectionPage.hasNextPage()) {
            System.out.println(collectionPage.hasPreviousPage());
            System.out.println(collectionPage.selectNextPage(selectSupplier));
        }
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        Supplier<Map<Integer, String>> selectSupplier2 = () -> sqlContext.select()
                .column(Product::getProductId)
                .column(Product::getProductName)
                .from(Product.class)
                .fetch(ProductView.class).toMap(ProductView::getProductId, ProductView::getProductName);
        MapPage<Integer, String, Map<Integer, String>> mapPage = PageHelper.ofMap(1, 5)
                .selectPage(selectSupplier2);
        System.out.println(mapPage);
        while (mapPage.hasNextPage()) {
            System.out.println(mapPage.hasPreviousPage());
            System.out.println(mapPage.selectNextPage(selectSupplier2));
        }
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        //还是带需要构建一个通用的对象
        Supplier<List<Product>> selectSupplier3 = () -> sqlContext.select()
                .allColumn()
                .from(Product.class)
                .where(condition -> condition.andGreaterThan(Product::getProductId, 100))
                .fetch().toList();
        PageInfo<List<Product>> listPageInfo = PageHelper.of(-999, 12).selectPage(selectSupplier3);
        System.out.println(listPageInfo);
        while (listPageInfo.hasNextPage()) {
            System.out.println(listPageInfo.hasPreviousPage());
            System.out.println(listPageInfo.selectNextPage(selectSupplier3));
        }
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        PageInfo<Map<Integer, String>> mapPageInfo = PageHelper.of(1, 9).selectPage(selectSupplier2);
        System.out.println(mapPageInfo);
        while (mapPageInfo.hasNextPage()) {
            System.out.println(mapPageInfo.hasPreviousPage());
            System.out.println(mapPageInfo.selectNextPage(selectSupplier2));
        }
    }

    List<ProductView> selectProductViewList() {
        return sqlContext.select()
                .allColumn()
                .from(Product.class)
                .fetch(ProductView.class).toList();
    }

    @Test
    void selectNextPage() {
        PageInfo<List<ProductView>> pageInfo = PageHelper.of(1, 3).selectPage(this::selectProductViewList);
        System.out.println("pageInfo: " + pageInfo);
        PageInfo<List<ProductView>> nextPage = pageInfo.selectNextPage(this::selectProductViewList);
        System.out.println("nextPage: " + nextPage);
        System.out.println("pageInfo: " + pageInfo);
    }

    @Test
    void selectNextPage2() {
        PageInfo<List<ProductView>> pageInfo = PageHelper.of(1, 3).selectPage(this::selectProductViewList);
        System.out.println("pageInfo: " + pageInfo);
        while (pageInfo.hasNextPage()) {
            pageInfo.selectNextPage();
            System.out.println("pageInfo: " + pageInfo);
        }
    }

    @Test
    void select4() {
        List<ProductView> list = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .fetch(ProductView.class).toList();
        Map<String, List<ProductView>> collect = list.stream().collect(Collectors.groupingBy(ProductView::getProductName));
        collect.forEach((k, v) -> System.out.println("最终结果：" + k + " --> " + v));
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
        Map<String, List<ProductView>> groupingBy = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .fetch(ProductView.class).toGroupingBy(ProductView::getProductName);
        groupingBy.forEach((k, v) -> System.out.println("最终结果：" + k + " --> " + v));
        System.out.println("======================================================");
        System.out.println("======================================================");
        System.out.println("======================================================");
//        ProductView one = sqlContext.select()
//                .allColumn()
//                .from(Product.class)
//                .fetch(ProductView.class).toOne();
//        System.out.println(one);

//        Integer one1 = sqlContext.select()
//                .allColumn()
//                .from(select -> select
//                                .column(new Count(1))
//                                .from(Product.class)
//                        , "_name")
//                .fetch(Integer.class).toOne();
//        System.out.println(one1);
        //return (u,v) -> { throw new IllegalStateException(String.format("Duplicate key %s", u)); };
        Map<String, ProductView> map = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .fetch(ProductView.class).toMap(ProductView::getProductName, v -> v, (v1, v2) -> {
                    System.out.println("V1 --> " + v1);
                    System.out.println("V2 --> " + v2);
                    return v1;
                });
        map.forEach((k, v) -> System.out.println("最终结果：" + k + " --> " + v));
    }

    @Test
    void select5() {
        Map<String, LinkedList<ProductView>> groupingBy = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .fetch(ProductView.class)
                .toGroupingBy(ProductView::getProductName, LinkedList::new, ConcurrentHashMap::new);
        System.out.println(groupingBy.size());
        groupingBy.forEach((k, v) -> {
            System.out.println(k + " --> " + v);
        });
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
        List<Object> list = sqlContext.select()
                .column("o", Order::getOrderId)
                .column("jt", Product::getProductName)
                .from(Order.class, "o")
                .join(() -> new JsonTable("o", Order::getOrderDetails, "$.items[*]",
                        JsonColumn.builder().column("product_name").dataType("VARCHAR(150)").jsonPath("$.product").build()
                ), "jt", null)
                .fetch().toList();
        System.out.println(list);
    }

    @Test
    void test6() {
        LocalDate one = sqlContext.select()
                .column(Product::getCreatedAt)
                .from(Product.class)
                .limit(1)
                .fetch(LocalDate.class).toOne();
        System.out.println(one);
    }

    @Test
    void test7() {
        Product product = sqlContext.select()
                .allColumn()
                .from(Product.class)
                .where(whereCondition -> whereCondition.andEqualTo(Product::getProductId, 7))
                .fetch().toOne();
        System.out.println(product);
        Product product2 = sqlContext.selectByPrimaryKey(Product.class, 7);
        System.out.println(product2);
    }

    @Test
    void test8() {
        ArrayList<Integer> arrayList = new ArrayList<>();
        arrayList.add(1);
        arrayList.add(2);
        arrayList.add(3);
        arrayList.add(4);
        arrayList.add(5);
        List<Product> products = sqlContext.selectByPrimaryKey(Product.class, arrayList);
        products.forEach(System.out::println);
    }

    @Test
    void test9() {
        List<Product> list = sqlContext.select()
                .column(Product::getProductId)
                .columnReference(columnReference())
                .from(Product.class)
                .fetch()
                .toList();
        list.forEach(System.out::println);
    }

    AbstractColumnReference columnReference() {
        return ColumnReference.withColumns()
                .column(Product::getProductId)
                .columnReference(columnReference2())
                .column(Product::getProductName);
    }

    AbstractColumnReference columnReference2() {
        return ColumnReference.withColumns()
                .column(Product::getAttributes)
                .column(Product::getCreatedAt);
    }

    @Test
    void testSelectPage() {
        PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
                .selectPage(() -> sqlContext.select().allColumn().from(User.class).where().fetch().toList());
        pageInfo.getRecords().forEach(System.out::println);
    }

    @Test
    void testSelectPageAppendWhere() {
        PageInfo<List<User>> pageInfo = PageHelper.of(1, 3)
                .applyWhere(whereCondition -> whereCondition.andGreaterThanOrEqualTo(bindName("userId"), 3))
                .selectPage(() -> sqlContext.select().allColumn().from(User.class).fetch().toList());
        pageInfo.getRecords().forEach(System.out::println);
        System.out.println("\n");
        PageInfo<List<User>> listPageInfo = pageInfo.selectNextPage(() -> sqlContext.select().allColumn().from(User.class).fetch().toList());
        listPageInfo.getRecords().forEach(System.out::println);
    }


    @Test
    void testOrderBy() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class)
                .orderBy(User::getRegistrationDate)
                .thenOrderBy(User::getUserId, SortOrder.DESC)
                .fetch().toList();
        list.forEach(System.out::println);
    }

    @Test
    void testDistinct() {
        List<User> list = sqlContext.select()
                .distinct()
                .allColumn()
                .from(User.class).fetch().toList();
        list.forEach(System.out::println);
    }

    @Test
    void testDistinct2() {
        List<String> list = sqlContext.select()
                .column(new Distinct(User::getName), "name")
                .from(User.class).fetch(String.class).toList();
        list.forEach(System.out::println);
    }

    @Test
    void testtoGroupingBy() {
        Map<Integer, List<User>> groupingBy = sqlContext.select()
                .distinct()
                .allColumn()
                .from(User.class)
                .fetch()
                .toGroupingBy(User::getUserId);
        System.out.println(groupingBy);
    }

    /**
     * 对查询结果组装为业务需要的对象
     */
    @Test
    void testtoGroupingBy2() {
        Map<Integer, HashSet<String>> groupingBy = sqlContext.select()
                .distinct()
                .allColumn()
                .from(User.class)
                .fetch()
                .toGroupingBy(User::getUserId,
                        user -> user.getName() + "_hello",
                        HashSet::new,
                        ConcurrentHashMap::new);
        System.out.println(groupingBy);
    }

    @Test
    void toMap() {
        Map<Integer, String> map = sqlContext.select()
                .distinct()
                .allColumn()
                .from(User.class)
                .fetch()
                .toMap(User::getUserId,
                        user -> user.getName() + "_hello");
        System.out.println(map);
    }

    @Test
    void toMap2() {
        try {
            Map<Integer, String> map = sqlContext.select()
                    .distinct()
                    .allColumn()
                    .from(User.class)
                    .fetch()
                    .toMap(user -> 123,
                            user -> user.getName() + "_hello");
            System.out.println(map);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }

    }

    @Test
    void selectList() {
        String sql = "SELECT x.* FROM dynamic_sql2.users x ";
        List<User> users = sqlContext.selectList(sql, User.class);
        users.forEach(System.out::println);
    }

    @Test
    void selectList2() {
        String sql = "SELECT x.* FROM dynamic_sql2.users x ";
        List<User> users = sqlContext.selectList(sql, User.class, null);
        users.forEach(System.out::println);
    }

    @Test
    void selectList3() {
        String sql = "SELECT x.user_id FROM dynamic_sql2.users x ";
        List<Integer> users = sqlContext.selectList(sql, Integer.class);
        users.forEach(System.out::println);
    }

    @Test
    void selectList3_1() {
        String sql = "SELECT x.user_id FROM dynamic_sql2.users x ";
        List<HelloTest> users = sqlContext.selectList(sql, HelloTest.class);
        users.forEach(System.out::println);
    }

    @Test
    void selectList4() {
        ParameterBinder parameterBinder = new ParameterBinder();
        String value = SqlUtils.registerValueWithKey(parameterBinder, 1);
        String sql = "SELECT x.user_id FROM users x where x.user_id = %s";
        String format = String.format(sql, value);
        System.out.println(format);
        List<Integer> users = sqlContext.selectList("dataSource", format, Integer.class, parameterBinder);
        users.forEach(System.out::println);
    }

    @Test
    void selectList5() {
        String sql = "SELECT x.* FROM dynamic_sql2.users x where x.user_id = 2";
        List<Object> users = sqlContext.selectList("dataSource", sql, Object.class);
        users.forEach(System.out::println);
    }

    @Test
    void selectOne() {
        try {
            String sql = "SELECT x.* FROM dynamic_sql2.users x where x.user_id in (1,2,3)";
            Object obj = sqlContext.selectOne("dataSource", sql, Object.class);
            System.out.println(obj);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    void oderBy() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class)
                .orderBy(User::getRegistrationDate)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy2() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy(User::getRegistrationDate, SortOrder.DESC)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy3() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class)
                .orderBy("users", User::getRegistrationDate)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy4() {
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy("u", User::getRegistrationDate, SortOrder.DESC)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy5() {
        //假设这个排序字段是由前端传入
        String sortField = "registrationDate";
        List<User> list = sqlContext.select()
                .allColumn()
                .from(select -> select.allColumn()
                                .from(User.class)
                        , "u")
                .orderBy("u", sortField, SortOrder.DESC)
                .fetch(User.class)
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy6() {
        //假设这个排序字段是由前端传入
        String sortField = "registrationDate";
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy(sortField + " desc")
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy7() {
        //假设这个排序字段是由前端传入
        String sortField = "registrationDate";
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy(sortField, SortOrder.ASC)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void oderBy8() {
        //假设这个排序字段是由前端传入
        String sortField = "registrationDate";
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy(1 != 1, sortField, SortOrder.DESC)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void thenOrderBy() {
        //假设这个排序字段是由前端传入
        String sortField = "registrationDate";
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy(1 != 1, sortField, SortOrder.DESC)
                .thenOrderBy(User::getUserId)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void thenOrderBy2() {
        //假设这个排序字段是由前端传入
        String sortField = "registrationDate";
        List<User> list = sqlContext.select()
                .allColumn()
                .from(User.class, "u")
                .orderBy(true, sortField, SortOrder.DESC)
                .thenOrderBy(false, User::getUserId)
                .thenOrderBy(true, User::getName)
                .thenOrderBy(false, User::getEmail)
                .thenOrderBy(true, User::getGender)
                .thenOrderBy(true, User::getPhoneNumber)
                .fetch()
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void testDateFormat() {
        Dual dual = sqlContext.select()
                .column(new DateFormat(new Now(), "%Y-%m"))
                .from(Dual.class)
                .fetch()
                .toOne();
        System.out.println(dual);
    }

    @Test
    void testDateFormat2() {
        YearMonth yearMonth = sqlContext.select()
                .column(new DateFormat(new Now(), "%Y-%m"))
                .from(Dual.class)
                .fetch(YearMonth.class)
                .toOne();
        System.out.println(yearMonth);
    }

    @Test
    void testDateFormat3() {
        //假设这个排序字段是由前端传入
        List<User> list = sqlContext.select()
                .column(User::getUserId)
                .column(new DateFormat(User::getRegistrationDate, "%Y-%m"))
                .from(User.class)
                .groupBy(User::getUserId)
                .groupBy(new DateFormat(User::getRegistrationDate, "%Y-%m"))
                .limit(10)
                .fetch(User.class)
                .toList();
        System.out.println(list.size());
        list.forEach(System.out::println);
    }

    @Test
    void testArithmetic() {
        Map<String, Object> one = sqlContext.select()
                .column(new Round(new Sum(User::getUserId), 3).divide(2))
                .column(new Round(new Sum(User::getUserId).divide(2), 3))
                .column(new Round(new Sum(User::getUserId).divide(new Count(User::getUserId)), 3))
                .from(User.class)
                .fetchOriginalMap()
                .toOne();
        one.forEach((k, v) -> System.out.println(k + " = " + v));
    }

    @Test
    void testOverOrder() {
        List<Map<String, Object>> list = sqlContext.select()
                .column(Product::getProductId)
                .column(Product::getProductName)
                .column(new DenseRank(), over -> over.orderBy(new Sum(Product::getPrice), SortOrder.DESC), "currentRankNum")
                .column(new Sum(Product::getPrice), "sumPrice")
                .from(Product.class)
                .groupBy(Product::getProductId)
                .orderBy("currentRankNum")
                .fetchOriginalMap()
                .toList();
        for (Map<String, Object> stringObjectMap : list) {
            stringObjectMap.forEach((k, v) -> System.out.println(k + " = " + v));
            System.out.println("\n");
        }
    }

    @Test
    void execute() {
        Object execute = sqlContext.execute("select * from users limit 10");
        System.out.println(execute);
    }

    @Test
    void execute2() {
        String createTable = "CREATE TABLE `t_business_operation_log` (\n" +
                "  `id` int NOT NULL AUTO_INCREMENT COMMENT 'ID',\n" +
                "  `request_id` varchar(32)  NOT NULL COMMENT '当前请求唯一标识，一个请求内可能会有多次数据操作',\n" +
                "  `thread_name` varchar(100)  NOT NULL COMMENT '线程名',\n" +
                "  `operation_type` enum('UPDATE','INSERT','DELETE','SELECT')  NOT NULL COMMENT '操作类型',\n" +
                "  `operation_code` varchar(50)  NOT NULL COMMENT '操作事件编码',\n" +
                "  `title` varchar(255)  NOT NULL COMMENT '业务标题，多级用短横线分割，按照功能大小排列',\n" +
                "  `business_key` varchar(32) COMMENT '业务数据键名',\n" +
                "  `business_value` varchar(255) COMMENT '业务数据键值',\n" +
                "  `request_data` text COMMENT '请求数据',\n" +
                "  `before_data` text COMMENT '变更前数据',\n" +
                "  `after_data` text COMMENT '变更后数据',\n" +
                "  `is_success` tinyint  NOT NULL COMMENT '操作是否成功',\n" +
                "  `company_id` int   COMMENT '公司ID',\n" +
                "  `team_id` int NOT NULL DEFAULT '-1' COMMENT '团队ID',\n" +
                "  `create_uuid` int NOT NULL COMMENT '创建人UUID',\n" +
                "  `last_create_time` timestamp  NULL COMMENT '上次创建时间',\n" +
                "  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',\n" +
                "  PRIMARY KEY (`id`),\n" +
                "  KEY `t_business_operation_log_operation_code_IDX` (`operation_code`,`business_key`,`business_value`) USING BTREE\n" +
                ") ENGINE=InnoDB  COMMENT='业务操作日志表';";
        Object execute = sqlContext.execute(createTable);
        System.out.println(execute);
    }

    @Test
    void execute3() {
        ParameterBinder parameterBinder = new ParameterBinder();
        String key = SqlUtils.registerValueWithKey(parameterBinder, 10);
        Object execute = sqlContext.execute("select * from users limit " + key, parameterBinder);
        System.out.println(execute);
    }

    @Test
    void execute4() {
        ParameterBinder parameterBinder = new ParameterBinder();
        String key = SqlUtils.registerValueWithKey(parameterBinder, 10);
        Object execute = sqlContext.execute("dataSource", "select * from users limit " + key, parameterBinder);
        System.out.println(execute);
    }

}




























