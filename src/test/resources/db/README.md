# Dynamic-SQL2 数据库测试脚本（待审阅）

数据库脚本已验证；新增 Java 测试及运行方式见 [测试说明](../../README.md)。history 保留不动。

## 本机验证记录

2026-10-31 已在本机 MySQL 8.4.7（127.0.0.1:3306）的独立数据库 `dynamic_sql2_test` 执行建表、初始化和只读核对。11 张表共 37 行样本；行数、订单聚合、订单明细金额、NULL/空值、JSON、无符号 BIGINT、BIT、生成列和空间类型的核对结果符合脚本预期。

重置 → 重新建表 → 初始化 → 核对的完整流程也已通过，测试库保留初始化后的数据，方便直接检查。

连接示例（交互输入密码，不将密码写入脚本）：

```bash
mysql --protocol=TCP -h 127.0.0.1 -P 3306 -u root -p dynamic_sql2_test
```

MySQL 8.4 会对 `TINYINT(1)` 发出整数显示宽度弃用警告；此列有意保留，用于后续 JDBC `tinyInt1isBit` 映射测试。当前验证仅涉及数据库 SQL，尚未验证框架/JDBC 映射。

## 执行方式

目标：MySQL 8.0.16+，InnoDB，utf8mb4；使用单独的空测试库。
通过本机 mysql 客户端选定测试数据库，按顺序执行：

1. mysql/01-schema.sql：建表，不会自动删表。
2. mysql/02-seed.sql：固定样本数据。只执行一次，不用 INSERT IGNORE 掩盖错误。
3. mysql/03-inspect.sql：只读核对，预期值写在查询注释中。

需要重新开始时，显式执行 mysql/00-reset.sql，再执行上述脚本。
不要使用 mysql --force；任何报错都应停止并排查。MySQL DDL 隐式提交，建表中途失败不会整体回滚。
脚本设置当前连接字符集及 UTC 时区；读取测试也应使用 UTC。未设置或修改全局配置。

## 关联模型

- ds_department.parent_id → 自关联树，含根、子节点和空部门。
- department → account：一对多；account 103 无部门，适合 LEFT JOIN NULL 测试。
- account → account_profile：一对零或一；100/101 有资料，102/103 无资料。
- account ↔ role：通过 account_role 多对多；100 有两个角色，角色 3 未分配。
- account → purchase_order → order_item → product：多级关联；100 有两单，101 有取消空单，102/103 无单；商品 202 未售。
- 外键统一采用默认限制策略，便于未来验证非法引用及删除顺序，不引入级联删除差异。
- account_role 使用复合主键，仅表示数据库测试场景，不代表框架已支持复合主键实体 CRUD。
- 相同创建时间、下单时间用于验证分页必须添加唯一键作为稳定排序的第二条件。

## 类型覆盖

通用语义表 ds_type_sample：SMALLINT/INT/BIGINT、DECIMAL(38,10)、FLOAT/DOUBLE、数值布尔、CHAR/VARCHAR/TEXT、BINARY/VARBINARY/BLOB、DATE/TIME(6)/DATETIME(6)/TIMESTAMP(6)、NULL 与默认值。
样本 1 是所有可空字段为 NULL；2 是零/空字符串/空二进制；3 含中文、emoji、单引号、换行、百分号、下划线及微秒；4/5 含整数和定点数上下界、日期边界。

MySQL 专属 ds_mysql_type_sample：TINYINT、TINYINT(1)、MEDIUMINT、全部整数 UNSIGNED、BIT(1/8/64)、YEAR、负数及超过 24 小时的 TIME、ENUM/SET、JSON、各级 TEXT/BLOB、生成列。
包含 unsigned BIGINT 最大值（应考虑 BigInteger）、JSON SQL NULL/JSON null/空对象/数组、二进制零字节及高位字节。
TINYINT(1) 应分别在 JDBC tinyInt1isBit=true/false 下验证；BIT 不应统一按 Integer 接收。
浮点值按容差比较，DECIMAL 按精确数值比较；CHAR 填充和空串语义需按数据库能力单独断言。
LOB 当前验证多种存储类型和内容，不包含超大数据压力测试；后续使用独立大数据用例生成，避免初始化脚本膨胀。

空间表 ds_mysql_spatial_sample：全部七种具体空间类型及通用 GEOMETRY，统一 SRID 0，含 NULL 和正常样本；后续另测 SRID 4326 与轴序差异。
生成列只读验证，写入时应忽略；数据库可存储某类型不等于框架当前已支持其映射。

## 后续多数据库扩展约定

按 db/oracle、db/db2、db/<数据库名> 增加物理脚本；保持通用业务表、列名、固定主键及业务预期一致。
通用 CRUD/关联契约和各方言生成 SQL 期望分开；不要通过替换 SQL 字符串模拟方言适配。
identity/sequence、自增回填、布尔、空字符串、JSON、LOB、时区、分页、空间类型、生成列和复合主键分别记录能力。
Oracle 空字符串与 NULL、各库 TIME 类型和精度等差异必须显式记录，不能要求所有物理类型完全相同。
国产数据库需要明确具体产品、版本和兼容模式后增加适配，不将其作为统一方言。
当前未提供其他数据库脚本，不表示已适配。

参考：https://dev.mysql.com/doc/refman/8.0/en/data-types.html
空间类型：https://dev.mysql.com/doc/refman/8.0/en/spatial-type-overview.html
