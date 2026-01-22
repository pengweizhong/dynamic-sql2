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
import java.util.List;

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

    public StringBuilder assemblyParameters(List<Object> params) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < params.size(); i++) {
            Object param = params.get(i);
            stringBuilder.append(param);
            if (param != null) {
                stringBuilder.append("(").append(param.getClass().getSimpleName()).append(")");
            }
            if (i != params.size() - 1) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder;
    }
}
