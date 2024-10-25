# Dynamic-SQL2

> 旧版`Dynamic-SQL`地址：https://github.com/pengweizhong/dynamic-sql

（正在逐步实现）Dynamic-SQL是一个高扩展性的 ORM 框架，支持自动化数据库差异处理和动态数据源切换。  
框架具备智能查询构建器，可以自动生成适配 MySQL、Oracle、PostgreSQL、SQL Server 等数据库的 SQL。  
支持复杂查询、多数据源管理、分布式事务和自动模式迁移。设计上采用领域驱动设计和领域特定语言（DSL），  
并提供缓存和异步反应式支持，旨在简化数据库操作并提高开发效率。未来将继续扩展对新数据库的支持。

# 设计原则

1. 自动化数据库差异处理：  
   • 方言机制：具备强大的方言处理能力，根据不同数据库自动生成合适的 SQL。支持 MySQL、Oracle 等常见数据库。  
   • 自动查询生成：对增删改查操作尽可能由框架自动生成 SQL，屏蔽数据库之间的差异。  
2. 动态数据源切换：  
   • 提供灵活的数据源管理能力，能够根据业务需求动态切换不同的数据源，并确保事务性和一致性。  
   • 多数据源支持：动态切换或平滑切换数据源，支持跨数据库的操作和事务管理，切换数据源而无需修改SQL，有效降低迁移成本。  
3. 高扩展性：  
   • 灵活扩展特定数据库的功能，尤其是在一些数据库特有的功能（如存储过程、序列、窗口函数、自定义函数）上，提供钩子或扩展点。  
   • 模块化设计，能够方便地接入新数据库类型或者定制新的方言规则。  
4. 智能查询构建器：  
   • 提供类似 QueryDSL 或 JPA Criteria 的查询构建器，开发者可以通过类型安全的方式构建查询，无需手写 SQL。  
   • 查询构建器允许嵌套条件、函数、动态条件、分页、排序、分组等复杂操作，同时对数据库差异（如分页、表连接）自动处理。  
5. 事务管理和分布式事务：  
   • 支持单一数据库的事务管理，同时针对多数据源的操作引入分布式事务支持。  
   • 自定义分布式事务管理器，确保在多个数据库之间操作时数据的一致性。   


# 设计细节

1. 领域驱动设计  
   采用 DDD（领域驱动设计）的思路，将业务逻辑、数据模型和持久化层紧密结合，  
   通过聚合根、实体、值对象等方式减少开发者在数据库访问上的认知负担。
2. 领域特定语言（DSL）  
   提供一套清晰简洁的 DSL，开发者可以通过类 SQL 语法构建查询，而不需要关心底层数据库实现。  
3. 缓存支持  
   提供二级实体缓存、查询缓存等功能，减少数据库访问压力。  
4. 异步和反应式支持    
   随着微服务和响应式架构的流行，框架具备异步和反应式数据库访问的能力，  
   支持基于 CompletableFuture 或 Reactor的非阻塞查询。

# 应对业务场景

1. 标准增删改查：支持基本的 CRUD 操作，且能适配不同的数据库。  
2. 复杂关联查询：支持复杂的表关联、嵌套查询等场景。  
3. 数据分页和排序：内置分页、排序功能，自动适配不同数据库的分页机制（如 MySQL 的 LIMIT、Oracle 的 ROWNUM 等）。  
4. 历史数据或审计功能：可以内置审计功能，自动记录实体的创建时间、更新时间、操作者等。  
5. 兼容多种数据库特性：支持数据库特有的功能（如 Oracle 的 sequence，PostgreSQL 的  
   jsonb），而且可以为特定数据库实现定制化的功能。  

# 未来扩展和维护

• 支持新的数据库：通过方言机制可以灵活地引入对新数据库的支持。  
• 框架插件化：轻松为其添加新功能或适配新的数据库。    

# 部分查询API（开发中）

```java

   /**
    * 从多个表中提取用户及其订单相关的信息，包括用户的总花费、订单数量、所购买的产品及其分类等；
    * <p/>
    * SQL生成的如下：
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
              .column(User::getUserId)
              .column(User::getName, "user_name")
              .column("user_total", "total_spent")
              .column("user_total", "total_orders")
              .column("p", Product::getProductName)
              .column("p", Product::getPrice)
              .column("p", Product::getStock)
              .column(Category::getCategoryName)
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
              .limit(0, 500)
              .fetch().toList();
   }

   /**
    * 简单的函数计算
    */
   @Test
    void select1() {
        // ROUND(COUNT(*) * 100.0 / (SELECT COUNT(*) FROM t_pro_ret_phased), 2) AS percentage
        ExamResult percentage = sqlContext.select()
                .column(new Round(new Count(1).multiply(100).divide(
                        nestedSelect -> {
                            nestedSelect.select().column(new Count(1)).from(Student.class);
                        }), 2), "percentage")
                .from(ExamResult.class)
                .orderByField(">10%", "5~10%", "0~5%", "0%", "<-10%")
                .fetch().toOne();
        System.out.println(percentage);
    }

   /**
    * 构建分页查询
    */
   @Test
   void select2() {
      //提取查询方法可以重复利用
      Supplier<List<Product>> selectSupplier = () -> sqlContext.select()
              .allColumn()
              .from(Product.class)
              .fetch().toList();
      //List 分页
      PageInfo<List<Product>> listPageInfo = PageHelper.of(1, 12).selectPage(selectSupplier);
      System.out.println(listPageInfo);
      while (listPageInfo.hasNextPage()) {
         System.out.println(listPageInfo.hasPreviousPage());
         System.out.println(listPageInfo.selectNextPage(selectSupplier));
      }
      System.out.println("======================================================");
      //Map 分页
      PageInfo<Map<Integer, String>> mapPageInfo = PageHelper.of(1, 9)
              .selectPage(() -> sqlContext.select()
                      .column(Product::getProductId)
                      .column(Product::getProductName)
                      .from(Product.class)
                      .fetch(ProductView.class).toMap(ProductView::getProductId, ProductView::getProductName));
      System.out.println(mapPageInfo);
   }

```
# 开源支持

感谢 [JetBrains](https://www.jetbrains.com/) 提供的开源许可证支持。
  
![jetbrains.png](jetbrains.png)
