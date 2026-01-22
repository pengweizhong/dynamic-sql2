/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.plugins.logger;

import com.dynamic.sql.context.properties.SqlLogProperties;

import java.util.Collection;

public abstract class AbstractSqlLog {
    public String getPrintDataSourceName(SqlLogProperties props, SqlLogContext ctx) {
        if (!props.isEnabled()) {
            return "";
        }
        if (props.isPrintDataSourceName()) {
            return ctx.getDataSourceName();
        }
        return "";
    }

    public String toLogValue(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof Collection) {
            Collection<?> collection = (Collection<?>) value;
            if (collection.size() == 1) {
                return collection.iterator().next().toString();
            }
            return ((Collection<?>) value).size() + "";
        }
        return value.toString();
    }
}
