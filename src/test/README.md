# Dynamic-SQL2 测试

## 运行

需要 JDK 8+、Maven。默认执行无需数据库的单元测试：

```bash
mvn test
```

MySQL 集成测试使用已初始化的独立库 `dynamic_sql2_test`。初始化方式见 [数据库脚本说明](resources/db/README.md)，按 `01-schema.sql`、`02-seed.sql` 顺序执行。测试不会自动删表或重置已有数据。

```bash
export DS_TEST_URL='jdbc:mysql://127.0.0.1:3306/dynamic_sql2_test'
export DS_TEST_USER='root'
read -rsp 'MySQL password: ' DS_TEST_PASSWORD
export DS_TEST_PASSWORD
mvn -Pmysql-it test
unset DS_TEST_PASSWORD
```

`mysql-it` 会执行单元测试和 MySQL 集成测试。缺少连接配置或数据库不可用会失败，不会静默跳过。默认测试 JVM 与数据库会话均使用 UTC，可用 `-Dtest.jvm.timezone=Asia/Shanghai` 验证 JVM 与数据库时区不同的场景；每个集成用例使用独立事务，框架与 JDBC 共用连接，结束时回滚并关闭。自增序列的消耗不会回滚，因此不断言新主键具体值。请不要并发修改此测试库，也不要并行执行集成用例。

Maven 仅编译新测试，运行 `com.dynamic.sql.testing` 下的 `*Test` 类；history 文件原样保留。每个测试类使用独立 JVM，隔离框架静态注册表；不要启用 JUnit 并行执行。

## 结构与边界

- `testing/unit`：参数绑定顺序、重复占位符、NULL、特殊字符，以及整数类型转换和越界拒绝。
- `testing/model`：数据库中立实体，不绑定 MySQL 数据源或 schema。
- `testing/contract/DatabaseContract`：共享行为断言。覆盖 CRUD、批量操作、参数化条件、LEFT JOIN、稳定分页、自关联/一对一/多对多/多级关联、数值边界、Unicode、二进制、日期时间及写入往返。
- `testing/mysql/MySqlContractTest`：连接及事务生命周期、自增主键、默认值、空值语义、数据库约束，以及 MySQL 专属类型核对。

通用契约通过框架 API 读写；MySQL 专属无符号整数、BIT、空间、枚举/集合和 TIME 微秒检查包含直接 JDBC 断言。这些 JDBC 检查证明数据库与驱动行为，不代表框架已经支持全部 Java 类型映射。

当前 MySQL 实体 TIME 使用 `LocalTime`，已覆盖标量、实体、别名、NULL 及微秒写入往返。TIMESTAMP 映射为 `LocalDateTime` 时表示数据库会话中的本地时间，不随 JVM 时区漂移；DATETIME 保持无时区的原值。原始 Map 的正常 TIME/TIMESTAMP 仍保留 JDBC `Time`/`Timestamp` 值。负数或超过 24 小时的 MySQL TIME 是时长，返回字符串；映射为 `LocalTime` 明确报错，不取模或截断。

BIT(1) 保持 0/1/NULL 语义，BIT(n>1) 返回原始字节，可显式映射到无符号 `BigInteger`。`tinyInt1isBit=true/false` 两种配置都测试 NULL 和非 0/1 数值。时间精度与会话时区修复目前只启用在 MySQL 执行器；其他数据库仍需实库验证。

## 后续数据库适配

增加 `resources/db/<dialect>` 的物理脚本，保持共享表/列和固定业务样本含义一致。新增继承 `DatabaseContract` 的实现，提供连接、框架注册和事务回滚，并通过独立 Maven profile 显式选择。数据库特有的 SQL 模板/分页/生成主键断言放在各方言实现中。

Oracle 空串、TIME、序列，DB2 的 identity，以及国产数据库的具体产品和兼容模式，需要各自声明能力，不能直接照搬 MySQL 专属断言。当前只提供 MySQL 实现；大 LOB 压测、全部类型的框架读写及其他数据库实现仍需后续补充。
