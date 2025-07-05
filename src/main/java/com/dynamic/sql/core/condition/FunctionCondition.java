/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.condition;

import com.dynamic.sql.core.Fn;
import com.dynamic.sql.core.column.function.ColumFunction;

public interface FunctionCondition<C extends FunctionCondition<C>> extends Condition<C> {

    <T, F> C andEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    C andEqualTo(Object value, ColumFunction columFunction);

    <T, F> C orEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    C orEqualTo(Object value, ColumFunction columFunction);

    <T, F> C andNotEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orNotEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andGreaterThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orGreaterThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andLessThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orLessThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andNotIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orNotIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andMatches(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orMatches(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C andFindInSet(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> C orFindInSet(Fn<T, F> fn, ColumFunction columFunction);

}
