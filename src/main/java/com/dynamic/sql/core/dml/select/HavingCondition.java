package com.dynamic.sql.core.dml.select;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.column.function.windows.aggregate.AggregateFunction;
import com.dynamic.sql.core.condition.Condition;

import java.util.function.Consumer;

public interface HavingCondition<C extends HavingCondition<C>> extends Condition<C> {

    C andEqualTo(AggregateFunction function, Object value);

    C andEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orEqualTo(AggregateFunction function, Object value);

    C orEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andNotEqualTo(AggregateFunction function, Object value);

    C andNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orNotEqualTo(AggregateFunction function, Object value);

    C orNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andGreaterThan(AggregateFunction function, Object value);

    C andGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orGreaterThan(AggregateFunction function, Object value);

    C orGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andGreaterThanOrEqualTo(AggregateFunction function, Object value);

    C andGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orGreaterThanOrEqualTo(AggregateFunction function, Object value);

    C orGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andLessThan(AggregateFunction function, Object value);

    C andLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orLessThan(AggregateFunction function, Object value);

    C orLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andLessThanOrEqualTo(AggregateFunction function, Object value);

    C andLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orLessThanOrEqualTo(AggregateFunction function, Object value);

    C orLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andIn(AggregateFunction function, Iterable<?> values);

    C andIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orIn(AggregateFunction function, Iterable<?> values);

    C orIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andNotIn(AggregateFunction function, Iterable<?> values);

    C andNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C orNotIn(AggregateFunction function, Iterable<?> values);

    C orNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    C andBetween(AggregateFunction function, Object start, Object end);

    C orBetween(AggregateFunction function, Object start, Object end);

    C andNotBetween(AggregateFunction function, Object start, Object end);

    C orNotBetween(AggregateFunction function, Object start, Object end);

}
