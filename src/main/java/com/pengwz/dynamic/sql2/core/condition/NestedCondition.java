package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public interface NestedCondition extends Condition {

    <T, F> NestedCondition andEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andNotEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orNotEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andLengthEquals(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orLengthEquals(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andLengthGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orLengthGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andLengthLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orLengthLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andLessThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orLessThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andNotIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orNotIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andContains(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orContains(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andAnyIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orAnyIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition andAllIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> NestedCondition orAllIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    NestedCondition andExists(Consumer<NestedSelect> nestedSelect);

    NestedCondition andNotExists(Consumer<NestedSelect> nestedSelect);

}
