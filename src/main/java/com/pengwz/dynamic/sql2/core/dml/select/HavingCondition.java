package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.column.function.aggregate.AggregateFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;

import java.util.function.Consumer;

public interface HavingCondition extends Condition {

    HavingCondition andEqualTo(AggregateFunction function, Object value);

    HavingCondition andEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orEqualTo(AggregateFunction function, Object value);

    HavingCondition orEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andNotEqualTo(AggregateFunction function, Object value);

    HavingCondition andNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orNotEqualTo(AggregateFunction function, Object value);

    HavingCondition orNotEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andGreaterThan(AggregateFunction function, Object value);

    HavingCondition andGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orGreaterThan(AggregateFunction function, Object value);

    HavingCondition orGreaterThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andGreaterThanOrEqualTo(AggregateFunction function, Object value);

    HavingCondition andGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orGreaterThanOrEqualTo(AggregateFunction function, Object value);

    HavingCondition orGreaterThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andLessThan(AggregateFunction function, Object value);

    HavingCondition andLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orLessThan(AggregateFunction function, Object value);

    HavingCondition orLessThan(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andLessThanOrEqualTo(AggregateFunction function, Object value);

    HavingCondition andLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orLessThanOrEqualTo(AggregateFunction function, Object value);

    HavingCondition orLessThanOrEqualTo(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andIn(AggregateFunction function, Iterable<?> values);

    HavingCondition andIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orIn(AggregateFunction function, Iterable<?> values);

    HavingCondition orIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andNotIn(AggregateFunction function, Iterable<?> values);

    HavingCondition andNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition orNotIn(AggregateFunction function, Iterable<?> values);

    HavingCondition orNotIn(AggregateFunction function, Consumer<AbstractColumnReference> nestedSelect);

    HavingCondition andBetween(AggregateFunction function, Object start, Object end);

    HavingCondition orBetween(AggregateFunction function, Object start, Object end);

    HavingCondition andNotBetween(AggregateFunction function, Object start, Object end);

    HavingCondition orNotBetween(AggregateFunction function, Object start, Object end);

}
