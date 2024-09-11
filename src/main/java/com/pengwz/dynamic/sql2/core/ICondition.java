package com.pengwz.dynamic.sql2.core;

import java.util.function.Consumer;

/**
 * SQL 查询条件接口，用于构建动态 SQL 查询条件。
 * <p>
 * 此接口提供了一组方法，以流式（链式）方式构建 SQL 查询条件，
 * 支持常见的 SQL 操作符和函数，例如相等、比较、范围、模糊匹配等。
 * 组合各种条件，来生成符合需求的动态 SQL 语句。接口设计上允许条件的组合与扩展，
 * 支持灵活的查询需求。
 * <p>
 * 每个方法都支持链式调用，使得构建复杂的查询条件变得更加简洁和直观。方法前缀（如 `and`, `or`）
 * 表示条件的逻辑关系，用以准确地描述查询的逻辑。
 * <p>
 * 具体方法的使用说明如下：
 * <ul>
 *     <li>{@code andEqualTo(Fn<T, F> fn, Object value)}：添加一个“等于”条件，并且将其与之前的条件通过 AND 逻辑连接。</li>
 *     <li>{@code orEqualTo(Fn<T, F> fn, Object value)}：添加一个“等于”条件，并且将其与之前的条件通过 OR 逻辑连接。</li>
 *     <li>{@code customCondition(String customClause, Object... params)}：允许添加自定义的 SQL 条件子句，以支持特殊的 SQL 语法和逻辑。</li>
 * </ul>
 * <p>
 * 使用此接口时，可以快速创建一个实现类或通过依赖注入使用实现类来进行查询条件的构建。
 * <p>
 * 示例：
 * <pre>
 * ICondition condition = new SqlConditionBuilder()
 *     .andEqualTo(User::getName, "John")
 *     .orGreaterThan(User::getAge, 30)
 *     .andCustomCondition("custom_column BETWEEN ? AND ?", 10, 20);
 * </pre>
 * 以上代码将构建一个包含“姓名等于 John”或者“年龄大于 30”并且“custom_column 在 10 到 20 之间”的查询条件。
 */
public interface ICondition {

    /**
     * 添加等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加不等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andNotEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加不等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orNotEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加长度等于条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param length 匹配的长度
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andLengthEquals(Fn<T, F> fn, int length);

    /**
     * 添加长度等于条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param length 匹配的长度
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orLengthEquals(Fn<T, F> fn, int length);

    /**
     * 添加长度大于条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param length 匹配的长度
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andLengthGreaterThan(Fn<T, F> fn, int length);

    /**
     * 添加长度大于条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param length 匹配的长度
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orLengthGreaterThan(Fn<T, F> fn, int length);

    /**
     * 添加长度小于条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param length 匹配的长度
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andLengthLessThan(Fn<T, F> fn, int length);

    /**
     * 添加长度小于条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param length 匹配的长度
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orLengthLessThan(Fn<T, F> fn, int length);

    /**
     * 添加字段为空条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIsEmpty(Fn<T, F> fn);

    /**
     * 添加字段为空条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIsEmpty(Fn<T, F> fn);

    /**
     * 添加字段非空条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIsNotEmpty(Fn<T, F> fn);

    /**
     * 添加字段非空条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIsNotEmpty(Fn<T, F> fn);

    /**
     * 添加字段为空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIsNull(Fn<T, F> fn);

    /**
     * 添加字段为空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIsNull(Fn<T, F> fn);

    /**
     * 添加字段非空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIsNotNull(Fn<T, F> fn);

    /**
     * 添加字段非空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIsNotNull(Fn<T, F> fn);

    /**
     * 添加字段大于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andGreaterThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段大于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orGreaterThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段大于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andLessThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orLessThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andLessThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orLessThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段不在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andNotIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段不在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orNotIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段不在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andNotBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段不在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orNotBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andLike(Fn<T, F> fn, Object pattern);

    /**
     * 添加字段匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orLike(Fn<T, F> fn, Object pattern);

    /**
     * 添加字段不匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andNotLike(Fn<T, F> fn, Object pattern);

    /**
     * 添加字段不匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orNotLike(Fn<T, F> fn, Object pattern);

    /**
     * 添加字段匹配正则表达式条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andMatches(Fn<T, F> fn, String regex);

    /**
     * 添加字段匹配正则表达式条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orMatches(Fn<T, F> fn, String regex);

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andFindInSet(Fn<T, F> fn, Object item);

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andFindInSet(Fn<T, F> fn, Object item, String separator);

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orFindInSet(Fn<T, F> fn, Object item);

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orFindInSet(Fn<T, F> fn, Object item, String separator);

    /**
     * 添加字段包含指定子串条件，并且运算。
     *
     * @param fn        用于获取字段值的函数
     * @param substring 指定的子串
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andContains(Fn<T, F> fn, String substring);

    /**
     * 添加字段包含指定子串条件，或运算。
     *
     * @param fn        用于获取字段值的函数
     * @param substring 指定的子串
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orContains(Fn<T, F> fn, String substring);

    /**
     * 添加字段在指定值集合中有任意值条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andAnyIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段在指定值集合中有任意值条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orAnyIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段在指定值集合中包含所有值条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andAllIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段在指定值集合中包含所有值条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orAllIn(Fn<T, F> fn, Iterable<?> values);

    /**
     * 添加字段为正值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIsPositive(Fn<T, F> fn);

    /**
     * 添加字段为正值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIsPositive(Fn<T, F> fn);

    /**
     * 添加字段为负值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition andIsNegative(Fn<T, F> fn);

    /**
     * 添加字段为负值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 ICondition 实例
     */
    <T, F> ICondition orIsNegative(Fn<T, F> fn);

    /**
     * 添加一组条件，并且运算。
     * <p>
     * 该方法允许将一组条件作为当前条件的子条件进行添加，并且将它们合并为一个结果。
     * 其中传入的 {@link Consumer} 对象接受一个 {@link ICondition} 实例，
     * 用于设置嵌套的条件组合。
     * <p>
     * 例如：
     * <pre>
     *     condition.andCondition(nestedCondition -> {
     *         nestedCondition.andEqualTo(SomeClass::getA, 1);
     *         nestedCondition.orCondition(innerCondition -> {
     *             innerCondition.andEqualTo(SomeClass::getB, 2);
     *             innerCondition.orEqualTo(SomeClass::getC, 2);
     *         });
     *     });
     * </pre>
     *
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link ICondition} 实例，以便实现链式调用
     */
    default ICondition andCondition(Consumer<ICondition> nestedCondition) {
        nestedCondition.accept(this);
        return this;
    }

    /**
     * 添加一个嵌套条件，或运算。
     *
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link ICondition} 实例，以便实现链式调用
     * @see this#andCondition(Consumer)
     */
    default ICondition orCondition(Consumer<ICondition> nestedCondition) {
        nestedCondition.accept(this);
        return this;
    }

    /**
     * 添加自定义 SQL 条件子句到查询中。
     * <p>
     * 使用此方法可以添加任何不在接口预定义方法中的 SQL 条件。适用于复杂的查询条件，或者数据库特定的 SQL 语法。
     * <p>
     * 例如，如果你需要添加类似 `AND column BETWEEN ? AND ?` 这样的条件，
     * 可以使用此方法来指定自定义的 SQL 子句和对应的参数。
     *
     * @param customClause 自定义的 SQL 条件子句，这里应包含一个有效的 SQL 片段，通常是一个条件表达式
     * @param params       条件子句中占位符的参数，按照自定义条件子句中占位符的顺序提供
     * @return 当前 ICondition 实例，以便进行链式调用
     * @throws IllegalArgumentException 如果提供的 SQL 条件子句不合法或者参数与子句中的占位符数量不匹配
     */
    ICondition customCondition(String customClause, Object... params);
}