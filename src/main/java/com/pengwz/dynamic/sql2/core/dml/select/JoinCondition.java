package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.column.function.TableFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.cte.CteTable;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface JoinCondition extends Fetchable {
    /**
     * 表示 INNER JOIN 连接。此方法是 {@link #innerJoin(Class, Consumer)} 的别名。
     *
     * @param clazz       表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象
     * @return 当前的 {@link JoinCondition} 实例
     */
    default JoinCondition join(Class<?> clazz, Consumer<Condition> onCondition) {
        return join(clazz, null, onCondition);
    }

    default JoinCondition join(Class<?> clazz, String alias, Consumer<Condition> onCondition) {
        return innerJoin(clazz, alias, onCondition);
    }

    default JoinCondition join(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition) {
        return innerJoin(nestedSelect, alias, onCondition);
    }

    default JoinCondition join(Supplier<TableFunction> tableFunction, String alias) {
        return innerJoin(tableFunction, alias, null);
    }

    default JoinCondition join(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition) {
        return innerJoin(tableFunction, alias, onCondition);
    }

    /**
     * 表示 INNER JOIN 连接，支持与 CTE 进行连接。
     * <p>
     * 使用 CTE（CommonTableExpression）作为连接的目标表进行 INNER JOIN。
     * <p>
     *
     * @param cte         需要连接的 CTE 实例，表示要与当前表进行 INNER JOIN 的 CTE 表。
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，定义当前表与 CTE 之间的连接条件。
     * @return 当前的 {@link JoinCondition} 实例，用于继续构建查询链。
     */
    default JoinCondition join(CteTable cte, Consumer<Condition> onCondition) {
        return innerJoin(cte, onCondition);
    }

    /**
     * 构建一个 INNER JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * 通过传入实体类和条件构建器动态生成 ON 条件，从而定义连接的逻辑。
     * <p>
     * 示例：
     * <pre>
     *     query.innerJoin(User.class, condition -> {
     *         condition.andEqualTo("userId", "orderUserId");
     *     });
     * </pre>
     *
     * @param clazz       需要连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link Condition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition innerJoin(Class<?> clazz, Consumer<Condition> onCondition);

    JoinCondition innerJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition innerJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition);

    JoinCondition innerJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition);

    JoinCondition innerJoin(CteTable cte, Consumer<Condition> onCondition);

    /**
     * 构建一个 LEFT JOIN 连接，用于将当前查询与另一个表进行左连接。
     * <p>
     * LEFT JOIN 返回左表中的所有记录，即使在右表中没有匹配的记录。
     * 通过传入实体类和条件构建器动态生成 ON 条件，从而定义连接的逻辑。
     * <p>
     * 示例：
     * <pre>
     *     query.leftJoin(Order.class, condition -> {
     *         condition.andEqualTo("orderId", "userOrderId");
     *     });
     * </pre>
     *
     * @param clazz       需要左连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link Condition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition leftJoin(Class<?> clazz, Consumer<Condition> onCondition);

    JoinCondition leftJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition leftJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition);

    JoinCondition leftJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition);

    JoinCondition leftJoin(CteTable cte, Consumer<Condition> onCondition);

    /**
     * 构建一个 RIGHT JOIN 连接，用于将当前查询与另一个表进行右连接。
     * <p>
     * RIGHT JOIN 返回右表中的所有记录，即使在左表中没有匹配的记录。
     * 通过传入实体类和条件构建器动态生成 ON 条件，从而定义连接的逻辑。
     * <p>
     * 示例：
     * <pre>
     *     query.rightJoin(Product.class, condition -> {
     *         condition.andEqualTo("productId", "orderProductId");
     *     });
     * </pre>
     *
     * @param clazz       需要右连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link Condition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition rightJoin(Class<?> clazz, Consumer<Condition> onCondition);

    JoinCondition rightJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition rightJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<Condition> onCondition);

    JoinCondition rightJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<Condition> onCondition);

    JoinCondition rightJoin(CteTable cte, Consumer<Condition> onCondition);

    /**
     * 表示 FULL JOIN 连接。
     * <p>
     * FULL JOIN 连接（或称为 FULL OUTER JOIN）用于返回两个表中的所有行。对于每一行在第一个表中找到的行，FULL JOIN
     * 会查找第二个表中与之匹配的行，如果找不到匹配的行，则在结果中将第二个表的相关列填充为 NULL。反之亦然。
     * <p>
     * 该方法指定一个实体类和一个 ON 条件，以便对表进行 FULL JOIN 操作。ON 条件用于指定两个表之间的连接条件。
     * 如果两个表在 ON 条件下存在匹配的行，则这些行会在结果中出现；如果没有匹配的行，结果中仍然会包含表中所有的行，
     * 未匹配的列将填充为 NULL。
     * <p>
     * 使用示例：
     * <pre>
     *     JoinCondition joinCondition = ...; // 获取 JoinCondition 实例
     *     joinCondition.fullJoin(AnotherEntity.class, condition -> {
     *         condition.andEqualTo(SomeEntity::getId, AnotherEntity::getForeignKey);
     *     });
     * </pre>
     * 在上述示例中，`AnotherEntity.class` 是要与当前表进行 FULL JOIN 的目标表的实体类。`condition` 是一个
     * {@link Consumer<Condition>} 对象，用于定义连接条件。在这个例子中，连接条件是将 `SomeEntity` 表的 `id`
     * 字段与 `AnotherEntity` 表的 `foreignKey` 字段进行匹配。
     *
     * @param clazz       表对应的实体类，表示要与当前表进行 FULL JOIN 的目标表。
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象。该对象接受一个 {@link Condition} 实例，
     *                    用于定义表之间的连接条件。
     * @return 当前的 {@link JoinCondition} 实例，以便实现链式调用，继续进行其他连接或查询操作。
     */
    JoinCondition fullJoin(Class<?> clazz, Consumer<Condition> onCondition);

    JoinCondition fullJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition fullJoin(CteTable cte, Consumer<Condition> onCondition);

    /**
     * 表示 CROSS JOIN 连接。
     * <p>
     * CROSS JOIN 连接（或称为笛卡尔积连接）用于返回两张表的所有可能的组合。它将第一张表的每一行与第二张表的每一行进行配对，
     * 生成一个包含所有可能行组合的结果集。由于结果集可能会非常大，通常情况下，CROSS JOIN 应该谨慎使用。
     * <p>
     * 该方法指定一个表实体类，以便对表进行 CROSS JOIN 操作。CROSS JOIN 不需要提供连接条件，因为它不涉及表之间的匹配，
     * 它生成的是表的笛卡尔积。
     * <p>
     * 使用示例：
     * <pre>
     *     JoinCondition joinCondition = ...; // 获取 JoinCondition 实例
     *     joinCondition.crossJoin(AnotherEntity.class);
     * </pre>
     * 在上述示例中，`AnotherEntity.class` 是要与当前表进行 CROSS JOIN 的实体类。该方法将生成当前表和 `AnotherEntity` 表
     * 之间的所有可能行组合。
     *
     * @param clazz 表对应的实体类，表示要与当前表进行 CROSS JOIN 的目标表。
     * @return 当前的 {@link JoinCondition} 实例，以便实现链式调用，继续进行其他连接或查询操作。
     */
    JoinCondition crossJoin(Class<?> clazz);

    JoinCondition crossJoin(CteTable cte);

    /**
     * 表示 SELF JOIN 连接。
     * <p>
     * SELF JOIN 允许将同一张表与自身进行连接。通常情况下，SELF JOIN 用于处理具有层级结构或自引用关系的数据，
     * 例如组织结构图、图表或其他具有父子关系的数据模型。在 SELF JOIN 中，为了区分同一张表的两个不同实例，
     * 需要为它们指定不同的别名，该别名不能与现有别名冲突。
     * <p>
     * 该方法允许指定一个别名，并通过 {@link Consumer} 对象来定义连接条件。连接条件用于指定如何将表的不同实例进行匹配。
     * <p>
     * 使用示例：
     * <pre>
     *     JoinCondition joinCondition = ...; // 获取 JoinCondition 实例
     *     joinCondition.selfJoin("alias1", on -> {
     *         on.andEqualTo(MyEntity::getParentId, MyEntity::getId); // 例如，连接条件为父 ID 等于子 ID
     *     });
     * </pre>
     * 在上述示例中，`"alias1"` 是表的别名。`on` 是一个 {@link Consumer} 对象，定义了连接条件
     * 通过将表的父 ID 字段与子 ID 字段进行比较。
     *
     * @param alias       用于给当前表设置的别名。这个别名在连接条件中用于区分表的不同实例。
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象。该对象接受一个 {@link Condition} 实例，
     *                    用于设置连接条件，例如字段匹配或其他逻辑条件。
     * @return 当前的 {@link JoinCondition} 实例，以便实现链式调用，继续进行其他连接或查询操作。
     */
    JoinCondition selfJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition leftSelfJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition rightSelfJoin(Class<?> clazz, String alias, Consumer<Condition> onCondition);

    JoinCondition selfJoin(CteTable cte, Consumer<Condition> onCondition);

    /**
     * 追加where条件
     *
     * @param condition 用于构建 where 条件的 {@link Consumer} 对象。
     * @return 返回表连接的实例
     */
    TableRelation<?> where(Consumer<WhereCondition> condition);//NOSONAR

    TableRelation<?> where();//NOSONAR

    /**
     * 限制查询结果的返回行数
     *
     * @param offset 需要跳过的行数
     * @param limit  返回的最大行数
     * @return 当前查询构建器的实例
     */
    Fetchable limit(int offset, int limit);

    /**
     * 限制查询结果的返回行数
     *
     * @param limit 返回的最大行数
     * @return 当前查询构建器的实例
     */
    Fetchable limit(int limit);
}