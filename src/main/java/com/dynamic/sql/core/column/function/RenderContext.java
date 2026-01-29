/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function;

import com.dynamic.sql.core.Version;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.model.TableAliasMapping;

import java.util.Map;

/**
 * SQL 渲染上下文（Rendering Context）。
 * <p>
 * 用于在渲染 SQL 片段时提供必要的环境信息，例如：
 * <ul>
 *     <li>目标数据源名称（用于多数据源场景）</li>
 *     <li>SQL 方言（决定函数、关键字、语法差异）</li>
 *     <li>框架版本（用于兼容性处理）</li>
 *     <li>表别名映射（用于生成正确的表前缀）</li>
 * </ul>
 * <p>
 * 渲染 SQL 时，所有依赖上下文的行为都应通过该对象获取。
 */
public class RenderContext {

    /**
     * 目标数据源名称
     */
    private String dataSourceName;

    /**
     * SQL 方言
     */
    private SqlDialect sqlDialect;

    /**
     * Dynamic SQL 框架版本
     */
    private Version version;

    /**
     * 表别名映射，用于渲染列或表引用
     */
    private Map<String, TableAliasMapping> aliasTableMap;

    public RenderContext() {
    }

    /**
     * 构造一个完整的渲染上下文。
     *
     * @param dataSourceName 数据源名称
     * @param sqlDialect     SQL 方言
     * @param version        框架版本
     * @param aliasTableMap  表别名映射
     */
    public RenderContext(String dataSourceName,
                         SqlDialect sqlDialect,
                         Version version,
                         Map<String, TableAliasMapping> aliasTableMap) {
        this.dataSourceName = dataSourceName;
        this.sqlDialect = sqlDialect;
        this.version = version;
        this.aliasTableMap = aliasTableMap;
    }

    public String getDataSourceName() {
        return dataSourceName;
    }

    public void setDataSourceName(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

    public SqlDialect getSqlDialect() {
        return sqlDialect;
    }

    public void setSqlDialect(SqlDialect sqlDialect) {
        this.sqlDialect = sqlDialect;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public Map<String, TableAliasMapping> getAliasTableMap() {
        return aliasTableMap;
    }

    public void setAliasTableMap(Map<String, TableAliasMapping> aliasTableMap) {
        this.aliasTableMap = aliasTableMap;
    }
}
