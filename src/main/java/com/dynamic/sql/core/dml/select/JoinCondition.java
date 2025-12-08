package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.column.function.TableFunction;
import com.dynamic.sql.core.condition.impl.dialect.GenericWhereCondition;
import com.dynamic.sql.core.dml.select.cte.CteTable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 定义用于构建 SQL JOIN 条件的接口。
 * 提供了多种 JOIN 类型（如 INNER JOIN、LEFT JOIN、RIGHT JOIN、FULL JOIN、CROSS JOIN）的操作方法。
 * 此接口支持动态生成 JOIN 条件和链式调用，用于构建复杂的 SQL 查询。
 */
public interface JoinCondition extends Fetchable {

    /**
     * INNER JOIN 的别名方法。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象
     * @return 当前 {@link JoinCondition} 实例，用于链式调用
     */
    default JoinCondition join(Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return join(clazz, null, onCondition);
    }

    default JoinCondition join(boolean isEffective, Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? join(clazz, null, onCondition) : this;
    }

    /**
     * INNER JOIN 的别名方法，允许指定表别名。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param alias       表的别名
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象
     * @return 当前 {@link JoinCondition} 实例
     */
    default JoinCondition join(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        return innerJoin(clazz, alias, onCondition);
    }

    default JoinCondition join(boolean isEffective, Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? innerJoin(clazz, alias, onCondition) : this;
    }

    /**
     * 使用嵌套查询的 INNER JOIN 方法。
     *
     * @param nestedSelect 嵌套查询的列构造器
     * @param alias        嵌套查询的别名
     * @param onCondition  用于构建 ON 条件的 {@link Consumer} 对象
     * @return 当前 {@link JoinCondition} 实例
     */
    default JoinCondition join(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        return innerJoin(nestedSelect, alias, onCondition);
    }

    default JoinCondition join(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? innerJoin(nestedSelect, alias, onCondition) : this;
    }

    /**
     * 使用表函数进行 INNER JOIN。
     *
     * @param tableFunction 表函数供应者
     * @param alias         表的别名
     * @return 当前 {@link JoinCondition} 实例
     */
    default JoinCondition join(Supplier<TableFunction> tableFunction, String alias) {
        return innerJoin(tableFunction, alias, null);
    }

    default JoinCondition join(boolean isEffective, Supplier<TableFunction> tableFunction, String alias) {
        return isEffective ? innerJoin(tableFunction, alias, null) : this;
    }

    /**
     * 使用表函数和条件进行 INNER JOIN。
     *
     * @param tableFunction 表函数供应者
     * @param alias         表的别名
     * @param onCondition   用于构建 ON 条件的 {@link Consumer} 对象
     * @return 当前 {@link JoinCondition} 实例
     */
    default JoinCondition join(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        return innerJoin(tableFunction, alias, onCondition);
    }

    default JoinCondition join(boolean isEffective, Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? innerJoin(tableFunction, alias, onCondition) : this;
    }

    /**
     * 使用 CTE 表进行 INNER JOIN。
     *
     * @param cte         公共表表达式（CTE）实例
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象
     * @return 当前 {@link JoinCondition} 实例
     */
    default JoinCondition join(CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        return innerJoin(cte, onCondition);
    }

    default JoinCondition join(boolean isEffective, CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? innerJoin(cte, onCondition) : this;
    }

    default JoinCondition innerJoin(boolean isEffective, Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? innerJoin(clazz, onCondition) : this;
    }

    /**
     * 构建一个 INNER JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * INNER JOIN 返回两个表中满足连接条件的所有记录。如果没有匹配的记录，则不会出现在结果集中。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition innerJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition);


    /**
     * 构建一个 INNER JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * INNER JOIN 返回两个表中满足连接条件的所有记录。如果没有匹配的记录，则不会出现在结果集中。
     * 此方法支持指定表别名。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param alias       表的别名，用于在查询中标识该表
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition innerJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition);

    /**
     * 构建一个 INNER JOIN 连接，用于将当前查询与子查询结果关联。
     * <p>
     * 子查询返回的结果集会作为临时表参与连接，支持指定别名和 ON 条件。
     *
     * @param nestedSelect 子查询构建器，用于生成临时表
     * @param alias        子查询结果的别名
     * @param onCondition  用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition innerJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition);

    /**
     * 构建一个 INNER JOIN 连接，用于将当前查询与表函数的结果关联。
     * <p>
     * 表函数的返回值作为临时表参与连接，支持指定别名和 ON 条件。
     *
     * @param tableFunction 表函数构建器，用于生成临时表
     * @param alias         表函数结果的别名
     * @param onCondition   用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition innerJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition);

    /**
     * 构建一个 INNER JOIN 连接，用于将当前查询与公共表表达式 (CTE) 结果关联。
     * <p>
     * CTE 的返回值作为临时表参与连接，支持 ON 条件。
     *
     * @param cte         公共表表达式实例
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition innerJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition);

    /**
     * 构建一个 LEFT JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * LEFT JOIN 返回左表中的所有记录，以及右表中满足连接条件的记录。如果右表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition leftJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition leftJoin(boolean isEffective, Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? leftJoin(clazz, onCondition) : this;
    }

    /**
     * 构建一个 LEFT JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * LEFT JOIN 返回左表中的所有记录，以及右表中满足连接条件的记录。如果右表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。此方法支持指定表别名。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param alias       表的别名，用于在查询中标识该表
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition leftJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition leftJoin(boolean isEffective, Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? leftJoin(clazz, alias, onCondition) : this;
    }

    /**
     * 构建一个 LEFT JOIN 连接，用于将当前查询与子查询结果关联。
     * <p>
     * 子查询返回的结果集会作为临时表参与连接，支持指定别名和 ON 条件。
     *
     * @param nestedSelect 子查询构建器，用于生成临时表
     * @param alias        子查询结果的别名
     * @param onCondition  用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition leftJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition leftJoin(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? leftJoin(nestedSelect, alias, onCondition) : this;
    }


    /**
     * 构建一个 LEFT JOIN 连接，用于将当前查询与表函数的结果关联。
     * <p>
     * 表函数的返回值作为临时表参与连接，支持指定别名和 ON 条件。
     *
     * @param tableFunction 表函数构建器，用于生成临时表
     * @param alias         表函数结果的别名
     * @param onCondition   用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition leftJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition leftJoin(boolean isEffective, Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? leftJoin(tableFunction, alias, onCondition) : this;
    }

    /**
     * 构建一个 LEFT JOIN 连接，用于将当前查询与公共表表达式 (CTE) 结果关联。
     * <p>
     * CTE 的返回值作为临时表参与连接，支持 ON 条件。
     *
     * @param cte         公共表表达式实例
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition leftJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition leftJoin(boolean isEffective, CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? leftJoin(cte, onCondition) : this;
    }

    /**
     * 构建一个 RIGHT JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * RIGHT JOIN 返回右表中的所有记录，以及左表中满足连接条件的记录。如果左表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition rightJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition rightJoin(boolean isEffective, Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? rightJoin(clazz, onCondition) : this;
    }

    /**
     * 构建一个 RIGHT JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * RIGHT JOIN 返回右表中的所有记录，以及左表中满足连接条件的记录。如果左表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。此方法支持指定表别名。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param alias       表的别名，用于在查询中标识该表
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition rightJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition rightJoin(boolean isEffective, Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? rightJoin(clazz, alias, onCondition) : this;
    }

    /**
     * 构建一个 RIGHT JOIN 连接，用于将当前查询与子查询结果关联。
     * <p>
     * 子查询返回的结果集会作为临时表参与连接，支持指定别名和 ON 条件。
     *
     * @param nestedSelect 子查询构建器，用于生成临时表
     * @param alias        子查询结果的别名
     * @param onCondition  用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition rightJoin(Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition rightJoin(boolean isEffective, Consumer<AbstractColumnReference> nestedSelect, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? rightJoin(nestedSelect, alias, onCondition) : this;
    }

    /**
     * 构建一个 RIGHT JOIN 连接，用于将当前查询与表函数的结果关联。
     * <p>
     * 表函数的返回值作为临时表参与连接，支持指定别名和 ON 条件。
     *
     * @param tableFunction 表函数构建器，用于生成临时表
     * @param alias         表函数结果的别名
     * @param onCondition   用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition rightJoin(Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition rightJoin(boolean isEffective, Supplier<TableFunction> tableFunction, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? rightJoin(tableFunction, alias, onCondition) : this;
    }

    /**
     * 构建一个 RIGHT JOIN 连接，用于将当前查询与公共表表达式 (CTE) 结果关联。
     * <p>
     * CTE 的返回值作为临时表参与连接，支持 ON 条件。
     *
     * @param cte         公共表表达式实例
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition rightJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition rightJoin(boolean isEffective, CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? rightJoin(cte, onCondition) : this;
    }

    default JoinCondition fullJoin(boolean isEffective, Class<?> clazz, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? fullJoin(clazz, onCondition) : this;
    }

    /**
     * 构建一个 FULL JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * FULL JOIN 返回左右表中所有记录，以及满足连接条件的记录。如果任一表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition fullJoin(Class<?> clazz, Consumer<GenericWhereCondition> onCondition);

    /**
     * 构建一个 FULL JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * FULL JOIN 返回左右表中所有记录，以及满足连接条件的记录。如果任一表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。此方法支持指定表别名。
     *
     * @param clazz       需要连接的表对应的实体类
     * @param alias       表的别名，用于在查询中标识该表
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition fullJoin(Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition fullJoin(boolean isEffective, Class<?> clazz, String alias, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? fullJoin(clazz, alias, onCondition) : this;
    }

    /**
     * 构建一个 FULL JOIN 连接，用于将当前查询与公共表表达式 (CTE) 的结果关联。
     * <p>
     * FULL JOIN 返回左右表中所有记录，以及满足连接条件的记录。如果任一表中没有匹配的记录，
     * 则结果集中该部分字段的值为 NULL。此方法用于支持 CTE 作为临时表参与连接。
     *
     * @param cte         公共表表达式实例
     * @param onCondition 用于构建 ON 条件的 {@link Consumer} 对象，通过 {@link GenericWhereCondition} 接口定义连接条件
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition fullJoin(CteTable cte, Consumer<GenericWhereCondition> onCondition);

    default JoinCondition fullJoin(boolean isEffective, CteTable cte, Consumer<GenericWhereCondition> onCondition) {
        return isEffective ? fullJoin(cte, onCondition) : this;
    }

    /**
     * 构建一个 CROSS JOIN 连接，用于将当前查询与另一个表关联。
     * <p>
     * CROSS JOIN 返回笛卡尔积，即左表和右表中每一条记录的所有组合。
     * 通常在没有显式连接条件时使用 CROSS JOIN，或者用于生成测试数据。
     *
     * @param clazz 需要连接的表对应的实体类
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition crossJoin(Class<?> clazz);

    default JoinCondition crossJoin(boolean isEffective, Class<?> clazz) {
        return isEffective ? crossJoin(clazz) : this;
    }

    /**
     * 构建一个 CROSS JOIN 连接，用于将当前查询与公共表表达式 (CTE) 的结果关联。
     * <p>
     * CROSS JOIN 返回笛卡尔积，即左表和右表中每一条记录的所有组合。
     * 此方法适用于使用公共表表达式作为数据源参与笛卡尔积操作的场景。
     *
     * @param cte 公共表表达式实例
     * @return 当前查询上下文的 {@link JoinCondition} 实例，用于继续构建查询链
     */
    JoinCondition crossJoin(CteTable cte);

    default JoinCondition crossJoin(boolean isEffective, CteTable cte) {
        return isEffective ? crossJoin(cte) : this;
    }

    /**
     * 追加 WHERE 条件。
     *
     * @param condition 用于构建 WHERE 条件的 {@link Consumer} 对象
     * @return 返回表连接实例
     */
    TableRelation<?> where(Consumer<GenericWhereCondition> condition); // NOSONAR

    TableRelation<?> where(boolean isEffective, Consumer<GenericWhereCondition> condition);

    /**
     * 空条件 WHERE，返回无条件的查询上下文。
     * 有时可能需要衔接使用，比如在直接调用分组时。
     *
     * @return 返回表连接实例
     */
    TableRelation<?> where(); // NOSONAR

    /**
     * 限制查询返回的行数。
     *
     * @param offset 跳过的行数
     * @param limit  返回的最大行数
     * @return 当前查询构建器的实例
     */
    Fetchable limit(int offset, int limit);

    Fetchable limit(boolean isEffective, int offset, int limit);

    /**
     * 限制查询返回的行数。
     *
     * @param limit 返回的最大行数
     * @return 当前查询构建器的实例
     */
    Fetchable limit(int limit);

    Fetchable limit(boolean isEffective, int limit);
}