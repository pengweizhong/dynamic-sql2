# 主键生成

GeneratedValue 仅作用于 Id 字段。AUTO 保持数据库自增及 JDBC 回填；SNOWFLAKE、CUSTOM 在 SQL 构建前生成空主键，普通/选择性/两种批量插入以及 upsert 共用入口。显式提供的主键不覆盖。数据库表不会被框架自动修改。

## 雪花 ID

启动时注册线程安全生成器，节点编号从部署配置读取：

    import com.dynamic.sql.plugins.id.IdGenerators;
    import com.dynamic.sql.plugins.id.SnowflakeIdGenerator;

    IdGenerators.register("snowflake", new SnowflakeIdGenerator(nodeId));

实体使用默认名称，或通过 generator 属性选择注册名称：

    import com.dynamic.sql.anno.GeneratedValue;
    import com.dynamic.sql.anno.Id;
    import com.dynamic.sql.enums.GenerationType;

    @Id
    @GeneratedValue(strategy = GenerationType.SNOWFLAKE)
    private Long id;

- 雪花字段支持 Long 和 String；数据库建议使用 BIGINT 主键。String 属性查询时转换回字符串。
- 布局为符号位零、41 位毫秒差、10 位节点、12 位序列。默认纪元为 2024-01-01 UTC，节点范围 0～1023，同毫秒每实例最多 4096 个值。
- 部署方必须保证同一纪元下各活动实例节点编号唯一；同一节点/纪元组合共享一个生成器实例，不能按实体或数据源重复创建相同节点的独立实例。框架不提供节点租约或自动分配服务。
- 未注册生成器时明确失败，不使用隐式节点 0。注册名称禁止覆盖，避免重置运行中的序列。
- 时钟回拨立即失败，追平后继续递增；序列耗尽最多等待十毫秒，停滞时钟明确失败，避免无限阻塞。纪元前及 41 位时间范围外明确拒绝。
- 所有节点必须使用相同纪元。自定义纪元可使用带 LongSupplier 时钟的构造器；生产时通常传 System::currentTimeMillis。
- 向已有 AUTO_INCREMENT 列显式写入较大 ID 会影响数据库自身计数，部署时应统一同一列的生成策略。
- 事务回滚或 SQL 失败不回收 ID；对象已获得的主键保持原值，重试会沿用它。不承诺连续无空洞编号。

## 自定义算法

注册扩展生成器：

    IdGenerators.register("business-id", context -> businessIdService.nextId());

实体声明：

    @Id
    @GeneratedValue(strategy = GenerationType.CUSTOM, generator = "business-id")
    private Long id;

生成器可读取 IdGenerationContext 的实体、ColumnMeta 和数据源名称。注册实例必须线程安全，返回值不得为 NULL，并必须能被字段转换器转换。生成器负责自身唯一性。CUSTOM 必须配置名称，不限定 UUID、号段或其他算法的实现方式。

生成回调应只负责分配 ID，避免在回调中递归调用同一实体的插入流程。AUTO、SEQUENCE 不进入应用生成路径；本轮未增加其他数据库序列执行的承诺。

## 验证

SnowflakeIdTest 覆盖位布局、并发唯一性、回拨恢复、序列耗尽、节点隔离、最大有效时间及注册覆盖拒绝。GeneratedValueTest 检查新增注解元数据。MySQL 测试覆盖普通/选择性/两种批量和 upsert 路径、已有 ID 保留、自定义算法以及 Long/String 映射；全套数据库测试回滚业务写入。

最终完整 mysql-it：默认客户端预编译、UTC JVM 和服务端预编译、Asia/Shanghai JVM 均为 482 项通过，零失败、零跳过。新增雪花算法单元测试 7 项、主键元数据 1 项、MySQL 应用生成路径 3 项；其中扩展返回 NULL 时确认不执行 SQL 并清理 ThreadLocal。
