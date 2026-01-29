/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.scalar.string;


import com.dynamic.sql.core.FieldFn;
import com.dynamic.sql.core.column.function.AbstractColumFunction;
import com.dynamic.sql.core.column.function.ColumnFunctionDecorator;
import com.dynamic.sql.core.column.function.RenderContext;
import com.dynamic.sql.enums.SqlDialect;
import com.dynamic.sql.utils.ExceptionUtils;
import com.dynamic.sql.utils.SqlUtils;

public class Md5 extends ColumnFunctionDecorator {
    String string;

    public Md5(AbstractColumFunction delegateFunction) {
        super(delegateFunction);
    }

    public <T, F> Md5(FieldFn<T, F> fn) {
        super(fn);
    }

    public <T, F> Md5(String tableAlias, FieldFn<T, F> fn) {
        super(tableAlias, fn);
    }

    public Md5(String string) {
        this.string = string;
    }


    @Override
    public String render(RenderContext context) {
        if (context.getSqlDialect() ==  SqlDialect.MYSQL) {
            if (string != null) {
                return "md5(" + SqlUtils.registerValueWithKey(parameterBinder, string) + ")";
            }
            return "md5(" + delegateFunction.render(context) + ")";
        }
        if (context.getSqlDialect() ==  SqlDialect.ORACLE) {
            //Oracle 11g 及以上版本支持该功能。
            if (context.getVersion().getMajorVersion() < 11) {
                throw ExceptionUtils.unsupportedFunctionException("RAWTOHEX", context.getVersion(), context.getSqlDialect());
            }
            return "RAWTOHEX(DBMS_CRYPTO.HASH(UTL_RAW.CAST_TO_RAW(" + delegateFunction.render(context) + "), 2)) ";
        }
        throw ExceptionUtils.unsupportedFunctionException("md5", context.getSqlDialect());
    }
}
