# 类型映射问题与修复记录

本轮针对 MySQL 8.4.7 / Connector/J 9.5.0，以下记录对应本批修复及回归测试。

| 问题 | 修复前复现 | 本轮处理 |
| --- | --- | --- |
| TINYINT/BIT SQL NULL 变成 0 | `nullableTinyAndBitStayNull` 期望 NULL，实际 0 | `getInt` 后检查 `wasNull`；两种 tinyInt1isBit 配置覆盖 NULL 与数值 2 |
| BIT(64) 高位溢出 | `wideBitKeepsAllUnsignedBits` 触发 Integer 超范围异常 | 多位 BIT 保留 byte[]，BigInteger 按无符号转换；覆盖全 1 的 64 位值和 BIT(8) |
| LocalTime 无法映射 | 标量及实体查询抛出 Time → LocalTime 转换错误 | MySQL 读取时附带原始时间文本，映射时保留微秒；恢复实体 LocalTime 断言，并增加写入往返 |
| TIMESTAMP 映射随 JVM 时区漂移 | UTC 会话、Asia/Shanghai JVM 下增加 8 小时 | LocalDateTime 按数据库会话时间映射；UTC、上海、纽约 JVM 默认时区断言及 +08:00 数据库会话检查 |
| MySQL 超范围 TIME | java.sql.Time 无法准确表示负数或 >=24h 时长 | 原始值保留为字符串；拒绝映射为 LocalTime，避免截断或取模 |

## 映射约定及兼容性

- `LocalDateTime` 没有时区信息：TIMESTAMP 的结果表示数据库当前会话里的墙上时间，不表示 UTC Instant；DATETIME 保持原始时间字段。
- 正常范围 TIME / TIMESTAMP 的原始 Map 结果仍是 JDBC Time / Timestamp；附加的时间信息只用于 LocalTime / LocalDateTime 映射。普通 Map、自定义结果转换器仍可读取原始 JDBC 值。
- BIT(1) 保留 Integer 0/1/NULL；多位 BIT 返回 byte[]，调用者可选择 BigInteger。此前依赖多位 BIT 自动成为 Integer 的调用需要调整接收类型。
- 数据库驱动返回的普通 java.sql.Time 转为 LocalTime 仅保证该对象已具有的秒精度；MySQL 查询链路额外保留文本，才可保证 TIME(6) 微秒。

## 后续验证范围

- Oracle、DB2 及具体国产数据库的驱动行为尚未实库验证；MySQL 时间信息保留逻辑不会自动用于 Oracle。
- Duration/Instant/OffsetDateTime 等额外 Java 类型不在本轮新增支持范围。
- 自定义转换器、复杂嵌套关系与时间字段组合，以及全部类型批量写入，仍需扩大测试覆盖。

## 类型 CRUD 矩阵新增发现

| 问题 | 复现证据 | 修复 |
| --- | --- | --- |
| Year 直接交给 JDBC 写入 | YEAR 字段 CRUD 新增报越界；其他 39 个字段通过 | 注册 Year 转换器，写入明确的整型年份，读取兼容驱动日期/数值 |
| YEAR 零值读取成 2000 | 数据库数值零的实体读取为 Year(2000) | MySQL YEAR 按 getInt 读取并检查 wasNull，避免驱动的日期解释；原始 Map 的 YEAR 也改为 Integer |
| Polygon 的 X/Y 交换 | 原始 WKT `(0 0,2 0,2 2,0 0)` 写回变为 `(0 0,0 2,2 2,0 0)` | 读取时按 X、Y 构造 Point；JDBC 比较真实落库 WKT |
| Polygon SRID 字节序及默认字节序 | 非零 SRID 与独立大小端载荷不一致，未指定字节序的业务对象无法可靠写入 | SRID 固定小端，载荷按标记读取，默认小端；补充独立二进制单元测试 |
| Polygon 损坏载荷未明确拒绝 | 长度和点数未经校验，未闭合环由模型自动补齐 | 分配集合前检查载荷长度与点数，拒绝非法字节序和未闭合环 |
| 服务端预编译读取负时长错误 | Connector/J 二进制协议将 `-838:59:59` 读成 `-794:59:59`，文本协议正常 | 对驱动返回的负 TIME 原始二进制按天数+小时数还原，保留可选微秒；落库核对由 SQL CAST 转成字符，避免再次经过驱动的缺陷路径 |
| Contains 函数 SQL 模板含语句分号 | 将 ST_Contains 作为 SELECT 表达式执行时，MySQL 在字段别名处报告语法错误 | 删除表达式末尾分号及无对应占位符的参数注册；新增函数级查询覆盖命中/未命中 |

完整场景和剩余边界见 [CRUD 覆盖清单](COVERAGE.md)。

## UNION 重复执行

同一 UNION 查询设置全局排序和分页后，第二次 fetch 将 ORDER BY/LIMIT 再次追加到实例 SQL，MySQL 报语法错误。执行时改用独立 SQL 和参数容器，保留分支模板不变；集成测试验证连续两次得到相同分页结果。

## 经纬度函数语义修正

Longitude/Latitude 原调用 ST_X/ST_Y，无法表达地理 SRS 的真实经纬度。现改为 ST_Longitude/ST_Latitude，并新增 X/Y 承接笛卡尔分量读取；这是有行为变化的修正。缺少第二几何入参的 Distance/DistanceSphere 旧单表达式构造器现在立即抛出英文 IllegalArgumentException，避免执行时空指针，通用二元表达式支持仍待补充。

## 列函数 P0 修复

- 算术表达式重载原有部分仅返回 this，列引用还在渲染前解析；现按顺序保存操作数，渲染时解析列与绑定值，并提供 Group 控制优先级。
- 原函数算术渲染可能将右侧表达式反复追加到可变 SQL；改为不修改结构的重复渲染。
- SubString、DateFormat 原在每次 render 注册新参数；改用统一的稳定绑定模型，并保持首个列表达式的实体映射来源。
- CASE 原为整文件注释，转换包只有接口；现实现两种 CASE 及常用条件函数、白名单类型转换。
- JsonExtract 路径原直接拼接，旧版本判断使用错误的组合条件，Oracle JSON_VALUE 无法保留提取对象语义；现绑定路径、按 5.7.8 比较版本，并拒绝未实现的方言。

## P1 新发现与修复

- COUNT 特殊渲染路径未处理窗口 OVER，现保留分区/排序/框架，避免窗口计数误变普通聚合。
- 无排序窗口曾生成空 ORDER BY，现仅在有排序项时追加。
- GROUP_CONCAT 分隔符直接拼接，现使用十六进制字面量，避免引号与反斜线改变 SQL 结构。
- JSON_TABLE 原未收集输入参数，列声明和路径直接拼接，ON EMPTY/ERROR 顺序与默认值表达也不完整；现复用输入绑定、校验类型和标识符，支持嵌套与行号，默认 JSON 标量安全编码。
- JSON_VALID 的服务端绑定参数会被 MySQL 提前当作 JSON 验证，非法文本因此报错而非返回零；现显式转字符后校验，文本与服务端预编译均通过回归。
- STR_TO_DATE、UNIX_TIMESTAMP、CONVERT_TZ 的服务端参数类型推断可带来显示精度差异；这是数据库表达式类型差异，测试采用显式 CAST 固定比较类型，不截断业务值。
