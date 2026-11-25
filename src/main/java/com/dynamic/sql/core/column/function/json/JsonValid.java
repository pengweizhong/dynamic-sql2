/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.column.function.json;

import com.dynamic.sql.core.column.function.FunctionExpression;
import com.dynamic.sql.core.column.function.scalar.conversion.Cast;
import com.dynamic.sql.core.column.function.scalar.conversion.CastTarget;

/**
 * 参数先按字符传输，避免服务端预编译先验证 JSON 而破坏 JSON_VALID 返回零的语义。
 */
public final class JsonValid extends FunctionExpression {
    public JsonValid(Object value) {
        super("JSON_VALID", new Cast(value, CastTarget.CHAR));
    }
}
