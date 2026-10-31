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
| MySQL 文本/二进制 | TINYTEXT、MEDIUMTEXT、LONGTEXT、TINYBLOB、MEDIUMBLOB、LONGBLOB | TINYTEXT/TINYBLOB 边界、MEDIUMTEXT/MEDIUMBLOB 16 MiB 最大值与超限拒绝、70000 字节读写、NULL |
| 生成列 | base_amount → doubled_amount | 更新输入后核对生成值，输入 NULL 后核对生成值 NULL；生成列未列入可写实体 |

以上共 40 个普通/专属字段。空间 `SpatialCrudMatrix` 再覆盖 8 个字段，执行同样的增查改、置空、删除闭环：

- POINT、LINESTRING、POLYGON：框架内置 Java 模型转换，JDBC 独立比较规范化 WKT 和 SRID。
- MULTIPOINT、MULTILINESTRING、MULTIPOLYGON、GEOMETRY、GEOMETRYCOLLECTION：原始空间二进制读写；这不表示框架已提供对应的高级 Java 几何模型。
- GEOMETRY 的更新包含 POINT → LINESTRING，验证通用几何列可以切换实际类型。
- 空间类型闭环使用 SRID 0；另验证 POINT 使用 SRID 3857 的插入、更新和读取。Polygon 大小端、SRID 字节序和损坏载荷由独立单元测试检查。
- 空间标量函数已在 SELECT 中执行验证：ST_Contains 命中/未命中、ST_Distance、ST_Distance_Sphere、ST_AsText 和 ST_SRID。

## 非法输入与操作语义

`InvalidTypeMatrix` 共 13 组，每组均检查新增失败无记录、更新失败原值不变，并核对数据库错误码：

- CHAR/VARCHAR、VARBINARY/BLOB、TINYTEXT/TINYBLOB 超长。
- DECIMAL、MEDIUMINT、无符号整数越界。
- 非法 JSON、ENUM、SET。

额外用例验证：全量/选择性插入的 NULL 与默认值，选择性更新跳过 NULL 与强制清空的区别，通用类型的 `insertBatch`/`insertMultiple`，两类批量 API 遇到唯一键冲突时的事务内部分执行差异、保存点回滚与连接恢复；批量失败分别在 `rewriteBatchedStatements` 开关下验证。另覆盖 JSON null/SQL NULL/空对象/空数组，以及跨 JVM 时区的 TIMESTAMP 新增与更新。

## 尚未宣称覆盖的范围

- 所有可能值和 SQL 模式；非法输入在严格模式验证。排序规则验证当前仅针对 `utf8mb4_bin` 的大小写区分与尾随空格保留。
- LONGTEXT/LONGBLOB 的物理容量上限、GB 级压力和流式读写；MEDIUMTEXT/MEDIUMBLOB 的 16 MiB 上限现已覆盖。
- BIT 以所有 Java 数值类型写入、JSON 对应任意自定义 Java 对象、更大行数的批量压力，以及 `continueBatchOnError` 等未覆盖 Connector/J 配置下失败批次的部分执行行为。
- YEAR=0000 的 SQL 年份语义通过数值零验证；其他数据库是否允许该值需按能力单独定义。
- 带孔 Polygon 的高级模型、空间索引、SRID 4326 的实库轴序/地理运算；框架目前只公开并测试上述空间标量函数，未覆盖任意空间谓词组合。
- 框架自动识别并排除任意生成列；本轮实体明确省略生成列，另用 JDBC 核对生成结果。
- Oracle、DB2 及国产数据库；MySQL 专属用例不作为其他方言契约。

新增类型或映射方式时，应扩展对应矩阵和独立核对，不能仅增加初始化 SQL 就标记为框架已支持。

## 本轮验证结果

- 默认客户端预编译、UTC JVM：完整 `mysql-it` 152 项通过，其中 MySQL 集成类 138 项。
- 服务端预编译、Asia/Shanghai JVM：完整 `mysql-it` 152 项通过，其中 MySQL 集成类 138 项。
- 默认无需数据库的测试：14 项。

所有 MySQL 用例均回滚测试写入；测试报告的通过数只说明上述明确定义的场景，不等同于穷尽 MySQL 的所有类型组合。

## 业务操作补充验证

新增条件更新仅影响匹配账户、未命中更新和删除返回零、按唯一业务字段删除临时账户；验证跨表多步骤写入回滚至保存点后恢复原金额及记录集合。唯一业务键冲突后原账户保持不变，连接仍可继续写入。部门 upsert 覆盖首次新增、同主键更新，以及批量中混合新增和更新，最后按多个主键删除。

本批默认客户端预编译、UTC JVM 的完整 mysql-it 为 156 项通过，其中 MySQL 集成类 142 项；新增业务用例尚未在服务端预编译组合重跑。上述结果不代表已穷尽前述剩余范围。

## 查询专项补充

本批新增 14 个查询场景，均通过真实 MySQL 的 DSL 查询执行：等于、不等于、IN、NOT IN、包含端点的 BETWEEN、大于、IS NULL、IS NOT NULL、OR、禁用条件、无匹配结果；DISTINCT 部门投影、分页越界和首行；toOne 对无结果返回 NULL、唯一结果准确返回、多结果明确抛错；COUNT 总数与 GROUP BY 分组计数。条件矩阵核对完整主键集合，避免仅依赖行数。

默认客户端预编译、UTC JVM 的完整 mysql-it 已通过 170 项，其中 MySQL 集成类 156 项。查询专项新增用例尚未在其他驱动配置重跑。HAVING、子查询、UNION、窗口函数和更多关联组合仍需补充，不能将本批结果视为所有查询功能均已验证。

## 复杂查询补充验证

新增 HAVING 分组后筛选、内外层分别绑定参数的 IN 子查询、UNION 去重与 UNION ALL 保留重复，以及按唯一主键排序的 ROW_NUMBER 窗口查询。断言核对具体结果，包括 UNION 重叠行的重复次数。

本批默认客户端预编译、UTC JVM 完整 mysql-it 174 项通过，其中 MySQL 集成类 160 项，零失败、零跳过。已覆盖上述能力的代表场景；相关子查询、派生表、多级窗口及复杂关联的所有组合仍未穷尽。

## 查询边界补充验证

新增空子查询的 IN、EXISTS、NOT EXISTS，NOT IN 对外层 NULL 的三值逻辑，左关联筛选无账户部门，LIKE 与降序排序，以及定点 SUM 和空集 SUM/COUNT。断言核对具体主键、顺序和金额。

默认客户端预编译、UTC JVM 与服务端预编译、Asia/Shanghai JVM 的完整 mysql-it 均通过 179 项，其中 MySQL 集成类 165 项，零失败、零跳过。本批未发现新的框架缺陷。

## UNION 专项验证

新增 17 个场景：UNION/UNION ALL 分别覆盖不相交、重叠、完全相同、首分支空、末分支空、全部为空和三个分支；补充全局排序分页及同一对象重复执行、NULL 投影的去重与保留、分支内排序分页和完整账户实体映射。比较完整多重集合，保证重复次数准确；无 ORDER BY 的查询不假设数据库输出顺序。

发现并修复同一 UNION 对象重复 fetch 时重复追加 ORDER BY/LIMIT 的问题。默认客户端预编译下新增基础矩阵及回归测试通过；最终包含 NULL 和分支分页测试的服务端预编译、Asia/Shanghai JVM 完整 mysql-it 为 196 项通过，其中 MySQL 集成类 182 项，零失败、零跳过。派生表嵌套、关联 UNION、混合数据类型、非法分支输入及复杂组合仍待专项扩展，不能宣称穷尽全部 UNION 场景。

## 用户示例组合回归

补充两个真实业务组合：扁平 UNION/UNION ALL 的三个分支包含独立排序和分页，外层以非表 BO 字段排序、分页并映射 BO，断言重复行差异；派生 fromUnionAll 再 JOIN 普通账户表、LEFT JOIN UNION 派生表，并叠加 t/s 别名条件、右侧 NULL 筛选及 BO 映射，确认左关联未命中时仍保留来源的两条重复记录。

默认客户端预编译、UTC JVM 的完整 mysql-it 为 198 项通过，其中 MySQL 集成类 184 项，零失败、零跳过。这两个新增组合尚未在服务端预编译配置重跑；本批未发现新框架缺陷。

## 空间函数首批扩展

新增 X/Y、Area、Length、GeometryType、Dimension、IsEmpty、IsValid、IsSimple、Centroid、SwapXY 的 MySQL 实库断言，包含 NULL 面积、嵌套 AsText、SRID 4326 轴序，以及非地理 SRS 读取经度的错误场景。Longitude/Latitude 改为 ST_Longitude/ST_Latitude，旧 SRID 0 坐标读取需改为 X/Y。Envelope、ConvexHull 已提供封装，尚未添加独立实库断言。

默认客户端预编译完整 mysql-it 200 项通过；本批尚未完成原建议表中所有函数，二元通用表达式、构造函数、关系谓词、SRID 设置及其余几何运算仍需推进。

## 空间关系表达式扩展

新增参数绑定的 GeomFromText，以及 Contains、Within、Intersects、Disjoint、Equals、Touches、Crosses、Overlaps 双几何表达式和双字段入口；Contains 保留原字段与 Point 构造器。16 个动态用例分别覆盖命中与未命中，重复执行同一表达式确认参数没有累积。真实 MySQL 默认配置完整 mysql-it 216 项通过，其中集成类 202 项，零失败、零跳过。双字段入口、NULL、不同 SRID 和错误几何输入的独立断言仍需补充；其他方言明确拒绝，不宣称支持。

## 空间关系边界验证

新增 19 个场景：八种关系函数的左右 NULL 共 16 组；双字段与带别名字段比较并验证空列；不同 SRID 的 3033 错误、非法 WKT 的 3037 错误及 Java 空操作数拒绝；Envelope/ConvexHull 嵌套 Area 确认面积。默认客户端预编译完整 mysql-it 235 项通过，其中集成类 221 项，零失败、零跳过。本批没有新增框架修复；服务端预编译尚未重跑此批新增场景。

## 距离、WKB 与 SRID 设置

新增通用双表达式 Distance/DistanceSphere、参数化 GeomFromWKB，以及 SRID 表达式设置重载。新增三个实库测试验证距离、线串标准 WKB 构造与 SRID 设置不改变坐标。完整 mysql-it 238 项通过。原建议范围逐项进度见 [空间函数进度](GEOMETRY_PROGRESS.md)。

## 空间函数剩余类别完成

坐标转换、几何集合运算、缓冲区策略、线和环及集合成员访问、九种 MBR 关系、GeoJSON、GeoHash、简化、验证、插值和高级距离均已提供基础实现与实库断言；补齐坐标设置、SRID 字段设置和距离可选参数。新增 51 个集成动态场景与 3 个无需数据库测试，两种预编译配置完整 mysql-it 均通过 292 项。逐项状态及支持边界以 [空间函数进度](GEOMETRY_PROGRESS.md) 最新表格为准，前面历史批次的“待实现”描述已被本轮结果更新。

## LineString 对象映射

MysqlSpatialSample.lineValue 已由 byte[] 改为框架 LineString。新增对象转换器及独立二进制单元测试，原空间 CRUD 矩阵改用对象核对，并额外验证直接创建业务对象、SRID 3857、更新及标量映射。最终服务端预编译、Asia/Shanghai JVM 完整 mysql-it 297 项通过，零失败、零跳过。

## 全空间对象与代码引用整理

MysqlSpatialSample 的八种空间字段均已使用标准对象，新增 Geometry、MultiPoint、MultiLineString、MultiPolygon、GeometryCollection。原矩阵完整验证对象 CRUD，补充直接构造业务对象的新增/更新和结构化几何单元测试。最终服务端预编译完整 mysql-it 301 项通过，零失败、零跳过。

源码和新测试统一优先使用头部 import 与短类名；反射字符串中的类全名保持必要的完整形式，history 不改动。

## 列函数 P0 基线完成

C01～C05 完成算术表达式、公共参数绑定、CASE/空值条件、类型转换及 JSON 路径与版本修复；增加 40 项 MySQL 集成动态场景和 6 项单元测试，覆盖 SELECT、WHERE、HAVING、ORDER BY、嵌套、别名和重复执行。默认客户端预编译及服务端预编译配置完整 mysql-it 均通过 347 项。具体实施与兼容变化见 [列函数待办](COLUMN_FUNCTION_TODO.md) 的 P0 完成记录。

## 全部 P1 基线完成

16 项 P1 基线覆盖字符串、日期数值、聚合、统计、窗口、JSON 标量及 JSON_TABLE。新增 114 个集成场景和 6 个单元测试；完整 mysql-it 两种预编译配置均通过 467 项，零失败、零跳过。逐项实施状态、函数清单与支持边界见 [列函数待办](COLUMN_FUNCTION_TODO.md) 的 P1 完成记录。
