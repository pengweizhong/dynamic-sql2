package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public interface INestedCondition extends ICondition {

    <T, F> INestedCondition andEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andNotEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orNotEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andLengthEquals(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orLengthEquals(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andLengthGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orLengthGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andLengthLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orLengthLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andLessThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orLessThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andNotIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orNotIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andContains(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orContains(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andAnyIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orAnyIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition andAllIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

    <T, F> INestedCondition orAllIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect);

}
