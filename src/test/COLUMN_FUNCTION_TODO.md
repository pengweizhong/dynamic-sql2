# 列表达式与函数待办清单

范围：com.dynamic.sql.core.column 及全部子包。以当前源码为实现状态依据，MySQL 8.4 为首个实库目标。P0 为基础正确性，P1 为常见业务能力，P2 为扩展。C01～C05 的 MySQL P0 与全部 16 项 P1 基线已完成并验证，P2 仍待推进；类文件存在不代表全部方言或参数组合已经验证。

| 编号 | 优先级 | 类别与初查情况 | 待实现或完善内容 | 验证重点 | 状态 |
| --- | --- | --- | --- | --- | --- |
| C01 | P0 | 算术表达式：部分表达式重载只返回 this | 完成操作数、括号优先级、参数合并与重复渲染 | 列/常量/子查询混算、NULL、除零、重复执行 | 已实现并验证 |
| C02 | P0 | 通用参数：各函数实现不一致 | 统一常量绑定、嵌套表达式和别名契约 | 注入文本、参数顺序、重复值、NULL、重渲染 | 已实现并验证 |
| C03 | P0 | 条件：CaseWhen.java 整体被注释 | CASE WHEN 两种形式、COALESCE、NULLIF、IFNULL、IF | 多分支、无 ELSE、NULL、混合返回类型 | 已实现并验证 |
| C04 | P0 | 转换：conversion 只有接口 | CAST、CONVERT，受控目标类型与字符集 | 精度、小数位、日期、二进制、非法类型及注入拒绝 | 已实现并验证 |
| C05 | P0 | JSON：已有 Extract/Unquote，路径直接拼接 | 路径绑定、版本判断修正、提取语义与多路径 | JSON null/SQL NULL、缺失路径、对象/数组、特殊字符 | 已实现并验证 |
| C06 | P1 | 字符串：已有 Upper/SubString/Length/Md5 | LOWER、CONCAT、CONCAT_WS、CHAR_LENGTH、OCTET_LENGTH | 中文/emoji 字节与字符、NULL、空串、嵌套 | 已实现并验证 |
| C07 | P1 | 字符串处理：缺少封装 | TRIM、LTRIM、RTRIM、REPLACE、LEFT、RIGHT、LPAD、RPAD | 空白、Unicode、补齐截断、边界及 NULL | 已实现并验证 |
| C08 | P1 | 字符串查找：缺少封装 | LOCATE、INSTR、SUBSTRING_INDEX、REVERSE、REPEAT | 找不到、起点、负索引、分隔符 | 已实现并验证 |
| C09 | P1 | 日期：仅 Now/DateFormat | CURRENT_DATE、CURRENT_TIME、UTC_TIMESTAMP、DATE、TIME、EXTRACT、YEAR、MONTH、DAY | 微秒、闰日、NULL、会话时区、查询内稳定性 | 已实现并验证 |
| C10 | P1 | 日期计算：缺少封装 | DATE_ADD、DATE_SUB、DATEDIFF、TIMESTAMPDIFF、LAST_DAY、STR_TO_DATE | 月末、闰年、负间隔、单位白名单、解析错误 | 已实现并验证 |
| C11 | P1 | 时间戳转换：缺少封装 | UNIX_TIMESTAMP、FROM_UNIXTIME、CONVERT_TZ | DST、时区表缺失、范围、JVM 与会话时区 | 已实现并验证 |
| C12 | P1 | 数值：已有 Abs/Ceiling/Floor/Mod/Round/Truncate | 完善重载，增加 SIGN、POWER、SQRT、EXP、LN、LOG | DECIMAL 精度、负数、舍入、定义域和浮点容差 | 已实现并验证 |
| C13 | P2 | 数学扩展：缺少封装 | PI、SIN、COS、TAN、ATAN2、RADIANS、DEGREES、RAND | 定义域、精度、种子与非确定性断言 | 待推进 |
| C14 | P1 | 比较标量：缺少封装 | GREATEST、LEAST、ISNULL | 排序规则、混合类型、NULL 传播 | 已实现并验证 |
| C15 | P1 | 聚合：已有基础函数和 GroupConcat | 表达式重载、DISTINCT、多列 COUNT、GROUP_CONCAT 排序/分隔符 | 空集、NULL、重复值、HAVING、截断和重复渲染 | 已实现并验证 |
| C16 | P1 | 聚合扩展：缺少封装 | JSON_ARRAYAGG、JSON_OBJECTAGG、BIT_AND、BIT_OR、BIT_XOR、ST_Collect | 空集、NULL 键、重复键、无序结果及空间 SRID | 已实现并验证 |
| C17 | P1 | 统计：已有 StdDev/Variance，需明确口径 | STDDEV_POP、STDDEV_SAMP、VAR_POP、VAR_SAMP | 总体/样本差异、单行、空集、NULL | 已实现并验证 |
| C18 | P1 | 窗口：已有编号/排名和 Over | 完善 PARTITION BY、ROWS/RANGE、窗口边界和重渲染 | 并列、分区重置、累计 SUM、首尾及 NULL | 已实现并验证 |
| C19 | P1 | 窗口扩展：缺少封装 | LAG、LEAD、FIRST_VALUE、LAST_VALUE、NTH_VALUE、NTILE、CUME_DIST、PERCENT_RANK | 默认值、偏移、单行、窗口框架和不支持选项 | 已实现并验证 |
| C20 | P1 | JSON 构造及属性：缺少封装 | JSON_OBJECT、JSON_ARRAY、JSON_TYPE、JSON_LENGTH、JSON_VALID、JSON_KEYS | 配对参数、NULL、嵌套、数值与字符串区别 | 已实现并验证 |
| C21 | P1 | JSON 修改和检索：缺少封装 | JSON_SET、JSON_INSERT、JSON_REPLACE、JSON_REMOVE、JSON_CONTAINS、JSON_CONTAINS_PATH、JSON_SEARCH、JSON_MERGE_PATCH | 多路径绑定顺序、数组、缺失路径、转义 | 已实现并验证 |
| C22 | P1 | 表函数：已有 JsonTable，需完整验收 | NESTED PATH、FOR ORDINALITY、ON EMPTY/ERROR 及 JOIN | 数组展开、嵌套、空数组、错误类型、别名、参数安全 | 已实现并验证 |
| C23 | P2 | 正则与编码：除 Md5 外缺少封装 | REGEXP_LIKE、REGEXP_REPLACE、REGEXP_SUBSTR、SHA2、HEX、UNHEX、TO_BASE64、FROM_BASE64 | Unicode、模式、无匹配、二进制、NULL | 待推进 |
| C24 | P2 | 信息及空间收尾 | VERSION、DATABASE、CONNECTION_ID；空间可选参数和构造重载一致性 | 版本能力、方言拒绝、绑定顺序及对象映射 | 待推进 |

## 推进规则

- 建议先完成 C01～C05，再推进字符串、日期、数值和聚合。C19 依赖 C18；C22 依赖 JSON 参数契约。
- 每项完成后更新状态、具体测试名称、验证结果与提交号，不用总测试数替代具体覆盖。
- 单元测试检查 SQL、绑定顺序和方言拒绝；MySQL 实库测试检查实际语义、结果映射及边界。
- 已有函数需补 SELECT、WHERE/HAVING、ORDER BY、嵌套和重复执行契约；语法关键字、目标类型、时间单位采用枚举或白名单。
- 中文注释，英文异常和日志，短类名与头部 import；允许暂存，未经授权不自动提交。
- Oracle、DB2 和国产数据库按能力分别适配，不承诺同名函数同义。空间详细进度见 [空间进度](GEOMETRY_PROGRESS.md)。
- 本清单以业务常用表达式为目标，未穷举全部 MySQL 内置函数；锁、休眠、文件读取和系统管理函数暂不纳入。
- 本轮仅建立待办文档，没有修改函数或运行测试。

函数语义参考 [MySQL 8.4 官方函数章节](https://dev.mysql.com/doc/refman/8.4/en/functions.html)，实现状态来自本仓库当前源码。

## P0 完成记录

本轮 C01～C05 已完成 MySQL 基线，尚未提交。

| 编号 | 实施结果 | 验证入口 |
| --- | --- | --- |
| C01 | 四则运算所有操作数重载、延迟列解析、Group 括号、重复执行稳定性 | P0ExpressionMatrix 的 add/subtract/multiply/divide、precedence、group、column、fieldOperand、subquery；P0ExpressionTest |
| C02 | Value、ExpressionArguments、FunctionExpression；空间可变参数、SubString、DateFormat 复用；首列来源及实体别名保持 | 重复常量与特殊文本绑定、重复渲染、隐式实体别名；原空间矩阵全量回归 |
| C03 | 简单/搜索 CASE、COALESCE、NULLIF、IFNULL、IF、受控 Comparison | 多分支、无 ELSE、NULL、特殊文本，WHERE/HAVING/ORDER BY 组合 |
| C04 | CAST、CONVERT 类型及 USING 字符集，CastTarget/Charset 白名单 | DECIMAL 舍入、SIGNED、DATE、字符集、NULL、非法类型参数拒绝 |
| C05 | JsonExtract 多路径绑定、JsonUnquote、5.7.8 版本边界、Oracle 明确拒绝错误替代 | JSON 文本/对象、多路径、缺失路径、JSON null/SQL NULL、特殊路径与实体映射 |

完整 mysql-it 默认客户端预编译、UTC JVM 和服务端预编译、Asia/Shanghai JVM 均为 347 项通过，其中 MySQL 集成 317 项、无需数据库 30 项。新增 P0 集成场景 40 项、单元测试 6 项。原业务、空间类型、UNION 等测试一并回归。

范围说明：链式算术按 SQL 标准优先级，需改变顺序时显式使用 Group；常量使用 Value，字段使用 Column。JSON 路径传未加 SQL 引号的原始路径；不再接受依赖手动 SQL 引号的旧用法。MySQL JSON_EXTRACT 的对象/数组语义不再用 Oracle JSON_VALUE 静默替代。P0 不表示完成 P1 的所有旧函数重载，也不声明其他数据库的完整适配。

## P1 完成记录

P0 已提交为 8a1a5c4。下列全部 16 项 P1 为本轮实现，尚未提交。

| 编号 | 实施与测试入口 |
| --- | --- |
| C06～C08 | 新增拼接、字符/字节长度、裁剪、替换、填充和查找等函数；P1ScalarMatrix 检查中文/emoji、NULL、空值、负索引和绑定顺序 |
| C09～C11 | 日期提取、受控间隔单位、日期差值、月末、解析、时间戳和时区转换；覆盖闰年、月末、负间隔、非法时区及命名时区 DST；小数秒精度只接受 0～6 |
| C12、C14 | 数学与比较函数、数值常量便捷重载和 ROUND 默认精度；验证定义域 NULL、负数截断、NULL 传播 |
| C15 | 多列 COUNT DISTINCT、GROUP_CONCAT 嵌套表达式与安全分隔符；验证排序、去重、截断警告、AVG/MIN/MAX |
| C16～C17 | JSON/位聚合、ST_Collect、总体与样本统计；验证空集、单行、NULL 键、重复键与不依赖聚合顺序的断言 |
| C18～C19 | Over 分区、ROWS/RANGE 边界、八种窗口扩展；修复 COUNT 漏掉 OVER、无排序窗口出现空 ORDER BY；测试并列排名、分区、偏移默认值及窗口 NULL |
| C20～C21 | JSON 构造、属性、路径修改和检索；验证键值数量、SQL NULL、无效 JSON、数组和多路径 |
| C22 | JSON_TABLE 嵌套、行号、ON EMPTY/ERROR、默认值和关联展开；路径及列类型校验、默认值安全编码 |

新增 P1 测试：P1ScalarMatrix 72 项、P1AggregateWindowMatrix 34 项、P1JsonTableMatrix 8 项、P1ExpressionTest 6 项。完整 mysql-it 在默认客户端预编译、UTC JVM 和服务端预编译、Asia/Shanghai JVM 均为 **467 项通过**，其中集成 431 项、无需数据库 36 项，零失败、零跳过。

### 支持边界与兼容说明

- 本轮“完成”指各待办的 MySQL 基础功能及代表场景完成，未穷尽所有参数组合，也未完成 P2。
- 函数参数中的列通过 Column 或嵌套表达式传递；绑定 SQL NULL 请显式使用 Value(null)，避免 Java 可变参数将裸 null 当成整个参数数组。
- JSON_TABLE 的路径语法要求文本字面量：普通引号安全编码；反斜线和 NUL 明确拒绝，不冒险依赖 SQL 转义模式。默认值支持 JSON 标量，通过 UTF-8 十六进制字面量编码。
- 窗口目前提供 ROWS/RANGE 的非负整数边界，不提供 GROUPS、排除子句及日期 INTERVAL RANGE 边界。MySQL 不支持的窗口能力不伪造实现。
- 日期函数绑定参数在服务端预编译中可能被推断为 DATETIME(6)，与字面量查询的显示精度不同；需要固定输出类型时显式 CAST。测试按明确的日期/数值目标类型比较。
- JSON_VALID 先转字符，避免服务端将参数提前推断为 JSON，导致非法输入在函数执行前报错。
- 本机命名时区数据可用，已验证 America/New_York 的夏令时转换；其他环境未安装时区表时 CONVERT_TZ 可能返回 NULL。
- 其他数据库仍按方言和能力单独适配；P1 不承诺 Oracle、DB2 或国产数据库的同名语义。
