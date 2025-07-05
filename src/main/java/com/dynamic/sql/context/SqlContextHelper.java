/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.context;


import com.dynamic.sql.context.properties.SchemaProperties;
import com.dynamic.sql.context.properties.SqlContextProperties;
import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.interceptor.SqlInterceptorChain;

public class SqlContextHelper {
    private SqlContextHelper() {
    }

    public static SqlContext createSqlContext(SqlContextProperties sqlContextProperties) {
        SqlContextConfigurer sqlContextConfigurer = createSqlContextConfigurer(sqlContextProperties);
        sqlContextConfigurer.initializeContext();
        sqlContextProperties.getInterceptors().forEach(SqlInterceptorChain.getInstance()::addInterceptor);
        return sqlContextConfigurer.getSqlContext();
    }

    public static SqlContextConfigurer createSqlContextConfigurer(SqlContextProperties sqlContextProperties) {
        return new SqlContextConfigurer(sqlContextProperties, new DefaultSqlContext());
    }

    public static void addSchemaProperties(SqlContextProperties sqlContextProperties) {
        for (SchemaProperties schemaProperty : sqlContextProperties.getSchemaProperties()) {
            SchemaContextHolder.addSchemaProperties(schemaProperty);
        }
    }
}
