package com.dynamic.sql.plugins.pagination;


import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.dml.SqlStatementWrapper;

/**
 * 分页方言接口，用于定义不同数据库的分页策略。
 * 该接口提供了两个核心方法：统计总记录数的 SQL 构建和分页 SQL 的修改。
 */
public interface DialectPagination {

    /**
     * 构建统计总记录数的 SQL。
     *
     * @param version             数据库版本信息，用于支持针对不同版本的 SQL 优化。
     * @param sqlStatementWrapper 原始 SQL 语句的包装对象，包含查询的核心 SQL 和元数据信息。
     * @param abstractPage        分页信息。
     * @return 构建的统计总记录数的 SQL 字符串（StringBuilder）。
     */
    StringBuilder selectCountSql(Version version,
                                 SqlStatementWrapper sqlStatementWrapper,
                                 AbstractPage abstractPage);

    /**
     * 修改原始 SQL 为分页查询的 SQL。
     * <p>
     * 根据数据库的方言和分页需求，在原始 SQL 基础上添加分页语法，
     * 生成可用于分页查询的 SQL 语句。
     *
     * @param version             数据库版本信息，用于支持针对不同版本的分页实现。
     * @param sqlStatementWrapper 原始 SQL 语句的包装对象，包含查询的核心 SQL 和元数据信息。
     * @param abstractPage        分页信息。
     */
    void modifyPagingSql(Version version,
                         SqlStatementWrapper sqlStatementWrapper,
                         AbstractPage abstractPage);
}
