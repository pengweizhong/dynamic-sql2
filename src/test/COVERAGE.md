# MySQL 类型 CRUD 覆盖清单

验证环境：MySQL 8.4.7、Connector/J 9.5.0、JDK 8。同时核对默认文本协议与 `useServerPrepStmts=true`，后者结合 Asia/Shanghai JVM 时区执行。所有用例使用独立测试库；动态用例以保存点隔离，普通用例结束后回滚。

## 已覆盖的闭环

`TypeCrudMatrix` 为每个字段单独执行普通与批量闭环：框架新增 → JDBC 核对 → 框架查询 → 框架更新 → JDBC 核对 → 框架查询 → 全量更新为 NULL → JDBC/框架核对 → 框架删除 → JDBC/框架确认不存在。普通闭环外，40 个字段分别执行两条记录的 `insertBatch` 和 `insertMultiple`，逐条核对后批量删除。

| 类型组 | 字段或物理类型 | 当前样本范围 |
| --- | --- | --- |
| 通用整数 | SMALLINT、INT、BIGINT | 有符号上下界，NULL |
| 定点/浮点 | DECIMAL(38,10)、FLOAT、DOUBLE | 精确上下界、正负值、浮点容差、NULL |
| 标志 | SMALLINT 数值标志 | 0、1、NULL |
| 字符 | CHAR(8)、VARCHAR(512)、TEXT | 中文、emoji、引号、换行、满长度、NULL；CHAR 不要求保留尾部填充 |
| 二进制 | BINARY(8)、VARBINARY(256)、BLOB | 零字节、高位字节、空数组、声明长度、BLOB 65535 字节、NULL |
| 日期时间 | DATE、TIME(6)、DATETIME(6)、TIMESTAMP(6) | 日期/日期时间上下界、闰日、微秒、NULL；TIMESTAMP 使用有效的 2000～2030 样本 |
| MySQL 整数 | TINYINT、TINYINT(1)、MEDIUMINT、五种 UNSIGNED 整数 | 有符号上下界、无符号最大值、TINYINT(1)=2、NULL |
| 位串 | BIT(1)、BIT(8)、BIT(64) | 0、非零位串、64 位全 1、NULL；多位 BIT 以 byte[] 写入 |
| 年份/时长 | YEAR、MySQL TIME | 1901、2155、额外 YEAR=0 回归、负微秒时长、±838:59:59、NULL |
| 枚举/集合/JSON | ENUM、SET、JSON | 字符串映射、组合集合、JSON 对象/数组、SQL NULL；额外区分 JSON null 和空容器 |
| MySQL 文本/二进制 | TINYTEXT、MEDIUMTEXT、LONGTEXT、TINYBLOB、MEDIUMBLOB、LONGBLOB | 短类型长度边界、空内容、较长文本、70000 字节二进制、NULL |
| 生成列 | base_amount → doubled_amount | 更新输入后核对生成值，输入 NULL 后核对生成值 NULL；生成列未列入可写实体 |

以上共 40 个普通/专属字段。空间 `SpatialCrudMatrix` 再覆盖 8 个字段，执行同样的增查改、置空、删除闭环：

- POINT、POLYGON：框架内置 Java 模型转换，JDBC 独立比较规范化 WKT 和 SRID。
- LINESTRING、MULTIPOINT、MULTILINESTRING、MULTIPOLYGON、GEOMETRY、GEOMETRYCOLLECTION：原始空间二进制读写；这不表示框架已提供对应的高级 Java 几何模型。
- GEOMETRY 的更新包含 POINT → LINESTRING，验证通用几何列可以切换实际类型。
- 空间实库场景使用 SRID 0；非零 SRID、大小端和损坏载荷由独立 Polygon 单元测试检查。

## 非法输入与操作语义

`InvalidTypeMatrix` 共 13 组，每组均检查新增失败无记录、更新失败原值不变，并核对数据库错误码：

- CHAR/VARCHAR、VARBINARY/BLOB、TINYTEXT/TINYBLOB 超长。
- DECIMAL、MEDIUMINT、无符号整数越界。
- 非法 JSON、ENUM、SET。

额外用例验证：全量/选择性插入的 NULL 与默认值，选择性更新跳过 NULL 与强制清空的区别，通用类型的 `insertBatch`/`insertMultiple`，JSON null/SQL NULL/空对象/空数组，以及跨 JVM 时区的 TIMESTAMP 新增与更新。

## 尚未宣称覆盖的范围

- 所有可能值、所有 SQL 模式和字符排序规则；目前非法输入用例固定当前连接为严格模式。
- MEDIUM/LONG LOB 的物理最大容量、GB 级压力和流式读写；本轮只覆盖功能性较大样本。
- BIT 以所有 Java 数值类型写入、JSON 对应任意自定义 Java 对象、所有类型组合的批量操作。
- YEAR=0000 的 SQL 年份语义通过数值零验证；其他数据库是否允许该值需按能力单独定义。
- 带孔 Polygon 的高级模型、空间索引、SRID 4326 的实库轴序/地理运算、全部空间谓词。
- 框架自动识别并排除任意生成列；本轮实体明确省略生成列，另用 JDBC 核对生成结果。
- Oracle、DB2 及国产数据库；MySQL 专属用例不作为其他方言契约。

新增类型或映射方式时，应扩展对应矩阵和独立核对，不能仅增加初始化 SQL 就标记为框架已支持。

## 本轮验证结果

- 默认客户端预编译、UTC JVM：普通 CRUD 矩阵完成时 106 项通过；追加批量矩阵后为 146 项。
- 服务端预编译、Asia/Shanghai JVM：追加批量矩阵后为 146 项，零失败、零跳过。
- 默认无需数据库的测试：14 项。

所有 MySQL 用例均回滚测试写入；测试报告的通过数只说明上述明确定义的场景，不等同于穷尽 MySQL 的所有类型组合。
