package com.dynamic.sql.model;

import com.dynamic.sql.anno.Table;

/**
 * 表示 SQL 查询中使用的虚拟 DUAL 表。
 * <p>
 * {@code DUAL} 类是一个轻量级实体，对应于 Oracle 和 MySQL 等数据库中常见的特殊单行单列表 "DUAL"。
 * 它在动态 SQL 查询中作为占位符使用，适用于无需实际表的场景，例如计算表达式或调用数据库函数
 * （如 {@code DATE_FORMAT} 或 {@code NOW()}）。
 * </p>
 * <p>
 * 该类使用 {@link Table} 注解映射到 "DUAL" 表，使其能在需要实体的 SQL 生成框架中使用。
 * 它不包含任何字段，因为 DUAL 表的结构由数据库隐式处理。
 * </p>
 * 若执行{@code fetch}操作，则需要明确指定具体类型；否则，将继续返回没有任何意义的空对像。
 *
 * @since 0.1.0
 */
@Table("DUAL")
public class Dual {
}
