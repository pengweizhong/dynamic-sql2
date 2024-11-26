package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;

import java.util.function.Consumer;

public interface NestedCondition extends Condition {

    <T, F> NestedCondition andEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition andNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> NestedCondition orNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    NestedCondition andExists(Consumer<AbstractColumnReference> nestedSelect);

    NestedCondition orExists(Consumer<AbstractColumnReference> nestedSelect);

    NestedCondition andNotExists(Consumer<AbstractColumnReference> nestedSelect);

    NestedCondition orNotExists(Consumer<AbstractColumnReference> nestedSelect);

}
