package com.pengwz.dynamic.sql2.core.dml.select;

import com.pengwz.dynamic.sql2.core.ICondition;
import com.pengwz.dynamic.sql2.core.column.function.IAggregateFunction;

import java.util.function.Consumer;

public interface IHavingCondition extends ICondition {

    IHavingCondition andEqualTo(IAggregateFunction function, Object value);

    IHavingCondition andEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orEqualTo(IAggregateFunction function, Object value);

    IHavingCondition orEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andNotEqualTo(IAggregateFunction function, Object value);

    IHavingCondition andNotEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orNotEqualTo(IAggregateFunction function, Object value);

    IHavingCondition orNotEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andGreaterThan(IAggregateFunction function, Object value);

    IHavingCondition andGreaterThan(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orGreaterThan(IAggregateFunction function, Object value);

    IHavingCondition orGreaterThan(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andGreaterThanOrEqualTo(IAggregateFunction function, Object value);

    IHavingCondition andGreaterThanOrEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orGreaterThanOrEqualTo(IAggregateFunction function, Object value);

    IHavingCondition orGreaterThanOrEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andLessThan(IAggregateFunction function, Object value);

    IHavingCondition andLessThan(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orLessThan(IAggregateFunction function, Object value);

    IHavingCondition orLessThan(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andLessThanOrEqualTo(IAggregateFunction function, Object value);

    IHavingCondition andLessThanOrEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orLessThanOrEqualTo(IAggregateFunction function, Object value);

    IHavingCondition orLessThanOrEqualTo(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andIn(IAggregateFunction function, Iterable<?> values);

    IHavingCondition andIn(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orIn(IAggregateFunction function, Iterable<?> values);

    IHavingCondition orIn(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andNotIn(IAggregateFunction function, Iterable<?> values);

    IHavingCondition andNotIn(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition orNotIn(IAggregateFunction function, Iterable<?> values);

    IHavingCondition orNotIn(IAggregateFunction function, Consumer<NestedSelect> nestedSelect);

    IHavingCondition andBetween(IAggregateFunction function, Object start, Object end);

    IHavingCondition orBetween(IAggregateFunction function, Object start, Object end);

    IHavingCondition andNotBetween(IAggregateFunction function, Object start, Object end);

    IHavingCondition orNotBetween(IAggregateFunction function, Object start, Object end);

    IHavingCondition andIsPositive(IAggregateFunction function);

    IHavingCondition orIsPositive(IAggregateFunction function);

    IHavingCondition andIsNegative(IAggregateFunction function);

    IHavingCondition orIsNegative(IAggregateFunction function);

}
