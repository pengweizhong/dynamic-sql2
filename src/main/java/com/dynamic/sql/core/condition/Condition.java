package com.dynamic.sql.core.condition;


import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.conventional.Column;
import com.dynamic.sql.core.column.function.ColumFunction;

import java.util.function.Consumer;

/**
 * SQL 查询条件接口，用于构建动态 SQL 查询条件。
 * 此接口同时支持 WHERE 和 ON 条件的使用。
 * <p>
 * 此接口提供了一组方法，以流式（链式）方式构建 SQL 查询条件，
 * 支持常见的 SQL 操作符和函数，例如相等、比较、范围、模糊匹配等。
 * 组合各种条件，来生成符合需求的动态 SQL 语句。接口设计上允许条件的组合与扩展，
 * 支持灵活的查询需求。
 * <p>
 * 每个方法都支持链式调用，使得构建复杂的查询条件变得更加简洁和直观。方法前缀（如 `and`, `or`）
 * 表示条件的逻辑关系，用以准确地描述查询的逻辑。
 */
public interface Condition {

    /**
     * 添加等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andEqualTo(Fn<T, F> fn, Object value);

    Condition andEqualTo(Column column, Object value);

    /**
     * 添加等值连接条件，并且运算。
     * <pre>
     *     on.andEqualTo(Student::getClassId, Class::getId);
     * </pre>
     * 这将会生成 SQL 中的 "ON Student.classId = Class.id" 条件。
     *
     * @param field1 第一个字段，来自第一个实体类
     * @param field2 第二个字段，来自第二个实体类
     * @param <T1>   第一个实体类类型
     * @param <T2>   第二个实体类类型
     * @param <F>    字段类型
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    <T1, T2, F> Condition andEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition andEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段等值连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition orEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition orEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加不等于条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段不等值连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition andNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition andNotEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加不等于条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段不等值连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition orNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition orNotEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加字段为空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andIsNull(Fn<T, F> fn);

    Condition andIsNull(ColumFunction columFunction, Object value);

    /**
     * 添加字段为空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orIsNull(Fn<T, F> fn);

    Condition orIsNull(ColumFunction columFunction, Object value);

    /**
     * 添加字段非空值条件，并且运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andIsNotNull(Fn<T, F> fn);

    Condition andIsNotNull(ColumFunction columFunction, Object value);

    /**
     * 添加字段非空值条件，或运算。
     *
     * @param fn  用于获取字段值的函数
     * @param <T> 实体类类型
     * @param <F> 字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orIsNotNull(Fn<T, F> fn);

    Condition orIsNotNull(ColumFunction columFunction, Object value);

    /**
     * 添加字段大于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andGreaterThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段大于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition andGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition andGreaterThan(ColumFunction columFunction, Object value);

    /**
     * 添加字段大于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orGreaterThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段大于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition orGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition orGreaterThan(ColumFunction columFunction, Object value);

    /**
     * 添加字段大于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段大于或等于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition andGreaterThanOrEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加字段大于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段大于或等于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition orGreaterThanOrEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加字段小于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andLessThan(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition andLessThan(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition andLessThan(ColumFunction columFunction, Object value);

    /**
     * 添加字段小于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orLessThan(Fn<T, F> fn, Object value);


    /**
     * 添加字段小于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition orLessThan(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition orLessThan(ColumFunction columFunction, Object value);

    /**
     * 添加字段小于或等于指定值条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andLessThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于或等于连接条件，并且运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition andLessThanOrEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加字段小于或等于指定值条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param value 匹配的值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orLessThanOrEqualTo(Fn<T, F> fn, Object value);

    /**
     * 添加字段小于或等于连接条件，或运算。
     *
     * @param <T1>   实体类类型1
     * @param <T2>   实体类类型2
     * @param <F>    字段类型
     * @param field1 用于获取第一个字段值的函数
     * @param field2 用于获取第二个字段值的函数
     * @return 当前 {@link Condition} 实例
     */
    <T1, T2, F> Condition orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2);

    Condition orLessThanOrEqualTo(ColumFunction columFunction, Object value);

    /**
     * 添加字段在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andIn(Fn<T, F> fn, Iterable<?> values);

    Condition andIn(Column column, Iterable<?> values);

    Condition andIn(ColumFunction columFunction, Iterable<?> values);

    /**
     * 添加字段在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orIn(Fn<T, F> fn, Iterable<?> values);

    Condition orIn(ColumFunction columFunction, Iterable<?> values);

    /**
     * 添加字段不在指定值集合中条件，并且运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotIn(Fn<T, F> fn, Iterable<?> values);

    Condition andNotIn(ColumFunction columFunction, Iterable<?> values);

    /**
     * 添加字段不在指定值集合中条件，或运算。
     *
     * @param fn     用于获取字段值的函数
     * @param values 匹配的值集合
     * @param <T>    实体类类型
     * @param <F>    字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotIn(Fn<T, F> fn, Iterable<?> values);

    Condition orNotIn(ColumFunction columFunction, Iterable<?> values);

    /**
     * 添加字段在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段 BETWEEN 连接条件，并且运算。
     * <p>
     * 该方法用于构建一个 BETWEEN 连接条件，将指定字段的值与起始值和结束值之间的范围进行比较。
     * 在调用此方法时，可以传入一个用于获取字段值的函数，以及两个用于获取起始值和结束值的函数。
     * 这将生成一个 BETWEEN 条件，将其与当前条件组合使用。
     * <p>
     * 例如，假设有两个表 `Order` 和 `Product`，可以使用此方法将 `Order` 表的某个字段与 `Product` 表中的起始和结束字段之间的范围进行比较：
     * <pre>
     *     condition.andBetween(
     *         Order::getOrderDate,           // Order 表的字段
     *         Product::getStartDate,         // Product 表中的起始字段
     *         Product::getEndDate            // Product 表中的结束字段
     *     );
     * </pre>
     * 这将生成类似于以下 SQL 条件：
     * <pre>
     *     Order.orderDate BETWEEN Product.startDate AND Product.endDate
     * </pre>
     *
     * @param <T1>       实体类类型1，表示第一个表的实体类
     * @param <T2>       实体类类型2，表示第二个表的实体类
     * @param <F>        字段类型，表示字段的数据类型
     * @param field1     用于获取字段值的函数，表示要进行 BETWEEN 比较的字段
     * @param startField 用于获取起始值的函数，表示范围的起始值
     * @param endField   用于获取结束值的函数，表示范围的结束值
     * @return 当前 {@link Condition} 实例，以便实现链式调用
     */
    <T1, T2, F> Condition andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField);

    /**
     * 添加字段在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段 BETWEEN 连接条件，或运算。
     *
     * @param <T1>       实体类类型1
     * @param <T2>       实体类类型2
     * @param <F>        字段类型
     * @param field1     用于获取字段值的函数
     * @param startField 用于获取起始值的函数
     * @param endField   用于获取结束值的函数
     * @return 当前 {@link Condition} 实例
     * @see this#andBetween(Fn, Fn, Fn)
     */
    <T1, T2, F> Condition orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField);

    /**
     * 添加字段不在指定范围内条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段不在指定范围内条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param start 范围起始值
     * @param end   范围结束值
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotBetween(Fn<T, F> fn, Object start, Object end);

    /**
     * 添加字段匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andLike(Fn<T, F> fn, String pattern);

    /**
     * 添加字段匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orLike(Fn<T, F> fn, String pattern);

    /**
     * 添加字段不匹配指定模式条件，并且运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andNotLike(Fn<T, F> fn, String pattern);

    /**
     * 添加字段不匹配指定模式条件，或运算。
     *
     * @param fn      用于获取字段值的函数
     * @param pattern 匹配的模式（如 SQL 的 LIKE 子句）
     * @param <T>     实体类类型
     * @param <F>     字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orNotLike(Fn<T, F> fn, String pattern);

    /**
     * 添加字段匹配正则表达式条件，并且运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andMatches(Fn<T, F> fn, String regex);

    /**
     * 添加字段匹配正则表达式条件，或运算。
     *
     * @param fn    用于获取字段值的函数
     * @param regex 正则表达式模式
     * @param <T>   实体类类型
     * @param <F>   字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orMatches(Fn<T, F> fn, String regex);

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andFindInSet(Fn<T, F> fn, Object item);

    /**
     * 添加字段在指定集合中条件，并且运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition andFindInSet(Fn<T, F> fn, Object item, String separator);

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数）。
     *
     * @param fn   用于获取字段值的函数
     * @param item 指定的项目
     * @param <T>  实体类类型
     * @param <F>  字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orFindInSet(Fn<T, F> fn, Object item);

    /**
     * 添加字段在指定集合中条件，或运算（使用 FIND_IN_SET 函数），并指定分隔符。
     *
     * @param fn        用于获取字段值的函数
     * @param item      指定的项目
     * @param separator 分隔符
     * @param <T>       实体类类型
     * @param <F>       字段类型
     * @return 当前 Condition 实例
     */
    <T, F> Condition orFindInSet(Fn<T, F> fn, Object item, String separator);


    /**
     * 限制查询结果的返回行数
     *
     * @param offset 需要跳过的行数
     * @param limit  返回的最大行数
     * @return 当前 Condition 实例
     */
    Condition limit(int offset, int limit);

    /**
     * 限制查询结果的返回行数
     *
     * @param limit 返回的最大行数
     * @return 当前 Condition 实例
     */
    Condition limit(int limit);

    /**
     * 添加一组条件，并且运算。
     * <p>
     * 该方法允许将一组条件作为当前条件的子条件进行添加，并且将它们合并为一个结果。
     * 其中传入的 {@link Consumer} 对象接受一个 {@link Condition} 实例，
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
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    default Condition andCondition(Consumer<Condition> nestedCondition) {
        nestedCondition.accept(this);
        return this;
    }

    /**
     * 添加一个嵌套条件，或运算。
     *
     * @param nestedCondition 用于配置嵌套条件的 {@link Consumer} 对象
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     * @see this#andCondition(Consumer)
     */
    default Condition orCondition(Consumer<Condition> nestedCondition) {
        nestedCondition.accept(this);
        return this;
    }

    //    /**
//     * 添加自定义 SQL 条件子句到查询中。
//     * <p>
//     * 使用此方法可以添加任何不在接口预定义方法中的 SQL 条件。适用于复杂的查询条件，或者数据库特定的 SQL 语法。
//     * <p>
//     * 例如，如果需要添加类似 `AND column BETWEEN ? AND ?` 这样的条件，
//     * 可以使用此方法来指定自定义的 SQL 子句和对应的参数。
//     *
//     * @param customClause 自定义的 SQL 条件子句，这里应包含一个有效的 SQL 片段，通常是一个条件表达式
//     * @param params       条件子句中占位符的参数，按照自定义条件子句中占位符的顺序提供
//     * @return 当前 Condition 实例，以便进行链式调用
//     * @throws IllegalArgumentException 如果提供的 SQL 条件子句不合法或者参数与子句中的占位符数量不匹配
//     */
//    Condition customCondition(String customClause, Object... params);

    /**
     * 使用 AND 逻辑连接一个列函数条件。
     * <p>
     * 该方法将指定的列函数条件与当前条件通过 AND 逻辑运算符连接，形成一个新的复合条件。
     * 适用于需要多个条件同时满足的查询场景。
     * </p>
     * 示例：查询指定的点是否包含在其中
     * <pre>
     *     {@code
     *         List<LocationEntity> list = sqlContext.select()
     *                 .allColumn()
     *                 .from(LocationEntity.class)
     *                 .where(whereCondition -> whereCondition.andFunction(new Contains(LocationEntity::getArea, new Point(5, 5))))
     *                 .fetch()
     *                 .toList();
     *         System.out.println(list.size());
     *     }
     * </pre>
     *
     * @param columFunction 要与当前条件通过 AND 连接的列函数条件，不能为空。
     *                      该参数通常表示对数据库列的某种操作或计算（例如 SUM、AVG 等）。
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    Condition andFunction(ColumFunction columFunction);


    /**
     * 使用 OR 逻辑连接一个列函数条件。
     * <p>
     * 该方法将指定的列函数条件与当前条件通过 OR 逻辑运算符连接，形成一个新的复合条件。
     * 适用于需要多个条件中至少一个满足的查询场景。
     * </p>
     *
     * @param columFunction 要与当前条件通过 OR 连接的列函数条件，不能为空。
     *                      该参数通常表示对数据库列的某种操作或计算（例如 MAX、MIN 等）。
     * @return 当前的 {@link Condition} 实例，以便实现链式调用
     */
    Condition orFunction(ColumFunction columFunction);

}