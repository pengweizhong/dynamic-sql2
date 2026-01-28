/*
 * Copyright (c) 2024 PengWeizhong. All Rights Reserved.
 *
 * This source code is licensed under the MIT License.
 * You may obtain a copy of the License at:
 * https://opensource.org/licenses/MIT
 *
 * See the LICENSE file in the project root for more information.
 */
package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;
import com.dynamic.sql.core.condition.Condition;

import java.util.function.Consumer;

public interface HavingCondition<C extends HavingCondition<C>> extends Condition<C> {

    C andEqualTo(AggregateFunction function, Object value);

    C andEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C orEqualTo(AggregateFunction function, Object value);

    C orEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C andNotEqualTo(AggregateFunction function, Object value);

    C andNotEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C orNotEqualTo(AggregateFunction function, Object value);

    C orNotEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C andGreaterThan(AggregateFunction function, Object value);

    C andGreaterThan(AggregateFunction function, SelectDsl nestedSelect);

    C orGreaterThan(AggregateFunction function, Object value);

    C orGreaterThan(AggregateFunction function, SelectDsl nestedSelect);

    C andGreaterThanOrEqualTo(AggregateFunction function, Object value);

    C andGreaterThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C orGreaterThanOrEqualTo(AggregateFunction function, Object value);

    C orGreaterThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C andLessThan(AggregateFunction function, Object value);

    C andLessThan(AggregateFunction function, SelectDsl nestedSelect);

    C orLessThan(AggregateFunction function, Object value);

    C orLessThan(AggregateFunction function, SelectDsl nestedSelect);

    C andLessThanOrEqualTo(AggregateFunction function, Object value);

    C andLessThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C orLessThanOrEqualTo(AggregateFunction function, Object value);

    C orLessThanOrEqualTo(AggregateFunction function, SelectDsl nestedSelect);

    C andIn(AggregateFunction function, Iterable<?> values);

    C andIn(AggregateFunction function, SelectDsl nestedSelect);

    C orIn(AggregateFunction function, Iterable<?> values);

    C orIn(AggregateFunction function, SelectDsl nestedSelect);

    C andNotIn(AggregateFunction function, Iterable<?> values);

    C andNotIn(AggregateFunction function, SelectDsl nestedSelect);

    C orNotIn(AggregateFunction function, Iterable<?> values);

    C orNotIn(AggregateFunction function, SelectDsl nestedSelect);

    C andBetween(AggregateFunction function, Object start, Object end);

    C orBetween(AggregateFunction function, Object start, Object end);

    C andNotBetween(AggregateFunction function, Object start, Object end);

    C orNotBetween(AggregateFunction function, Object start, Object end);

}
