package com.pengwz.dynamic.sql2.core.condition.impl;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;
import com.pengwz.dynamic.sql2.core.condition.Condition;
import com.pengwz.dynamic.sql2.core.condition.FunctionCondition;
import com.pengwz.dynamic.sql2.core.condition.NestedCondition;
import com.pengwz.dynamic.sql2.core.condition.WhereCondition;
import com.pengwz.dynamic.sql2.core.dml.select.NestedSelect;

import java.util.function.Consumer;

public class WhereConditionImpl implements WhereCondition {
    @Override
    public <T, F> FunctionCondition andEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andNotEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orNotEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLengthEquals(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLengthEquals(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLengthGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLengthGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLengthLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLengthLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orGreaterThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLessThan(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orNotIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andNotLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orNotLike(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orMatches(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction, String separator) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction, String separator) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andContains(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orContains(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andAnyIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orAnyIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition andAllIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> FunctionCondition orAllIn(Fn<T, F> fn, ColumFunction columFunction) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andNotEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orNotEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLengthEquals(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLengthEquals(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLengthGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLengthGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLengthLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLengthLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orGreaterThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLessThan(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andLessThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orLessThanOrEqualTo(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andNotIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orNotIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andContains(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orContains(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andAnyIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orAnyIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition andAllIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> NestedCondition orAllIn(Fn<T, F> fn, Consumer<NestedSelect> nestedSelect) {
        return null;
    }

    @Override
    public <T, F> Condition andEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andNotEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orNotEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orNotEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andLengthEquals(Fn<T, F> fn, int length) {
        return null;
    }

    @Override
    public <T, F> Condition orLengthEquals(Fn<T, F> fn, int length) {
        return null;
    }

    @Override
    public <T, F> Condition andLengthGreaterThan(Fn<T, F> fn, int length) {
        return null;
    }

    @Override
    public <T, F> Condition orLengthGreaterThan(Fn<T, F> fn, int length) {
        return null;
    }

    @Override
    public <T, F> Condition andLengthLessThan(Fn<T, F> fn, int length) {
        return null;
    }

    @Override
    public <T, F> Condition orLengthLessThan(Fn<T, F> fn, int length) {
        return null;
    }

    @Override
    public <T, F> Condition andIsEmpty(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsEmpty(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andIsNotEmpty(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNotEmpty(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andIsNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andIsNotNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNotNull(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andGreaterThan(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orGreaterThan(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orGreaterThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orGreaterThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orGreaterThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andLessThan(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orLessThan(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orLessThan(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition orLessThanOrEqualTo(Fn<T, F> fn, Object value) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orLessThanOrEqualTo(Fn<T1, F> field1, Fn<T2, F> field2) {
        return null;
    }

    @Override
    public <T, F> Condition andIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andNotIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orNotIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition andBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return null;
    }

    @Override
    public <T, F> Condition orBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T1, T2, F> Condition orBetween(Fn<T1, F> field1, Fn<T2, F> startField, Fn<T2, F> endField) {
        return null;
    }

    @Override
    public <T, F> Condition andNotBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T, F> Condition orNotBetween(Fn<T, F> fn, Object start, Object end) {
        return null;
    }

    @Override
    public <T, F> Condition andLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition orLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition andNotLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition orNotLike(Fn<T, F> fn, String pattern) {
        return null;
    }

    @Override
    public <T, F> Condition andMatches(Fn<T, F> fn, String regex) {
        return null;
    }

    @Override
    public <T, F> Condition orMatches(Fn<T, F> fn, String regex) {
        return null;
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item) {
        return null;
    }

    @Override
    public <T, F> Condition andFindInSet(Fn<T, F> fn, Object item, String separator) {
        return null;
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item) {
        return null;
    }

    @Override
    public <T, F> Condition orFindInSet(Fn<T, F> fn, Object item, String separator) {
        return null;
    }

    @Override
    public <T, F> Condition andContains(Fn<T, F> fn, String substring) {
        return null;
    }

    @Override
    public <T, F> Condition orContains(Fn<T, F> fn, String substring) {
        return null;
    }

    @Override
    public <T, F> Condition andAnyIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orAnyIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andAllIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition orAllIn(Fn<T, F> fn, Iterable<?> values) {
        return null;
    }

    @Override
    public <T, F> Condition andIsPositive(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsPositive(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition andIsNegative(Fn<T, F> fn) {
        return null;
    }

    @Override
    public <T, F> Condition orIsNegative(Fn<T, F> fn) {
        return null;
    }

    @Override
    public Condition limit(int offset, int limit) {
        return null;
    }

    @Override
    public Condition limit(int limit) {
        return null;
    }

    @Override
    public Condition customCondition(String customClause, Object... params) {
        return null;
    }
}
