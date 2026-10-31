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
