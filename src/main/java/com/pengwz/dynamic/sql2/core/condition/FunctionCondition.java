package com.pengwz.dynamic.sql2.core.condition;

import com.pengwz.dynamic.sql2.core.Fn;
import com.pengwz.dynamic.sql2.core.column.function.ColumFunction;

public interface FunctionCondition extends Condition {

    <T, F> FunctionCondition andEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    FunctionCondition andEqualTo(Object value, ColumFunction columFunction);

    <T, F> FunctionCondition orEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    FunctionCondition orEqualTo(Object value, ColumFunction columFunction);

    <T, F> FunctionCondition andNotEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orNotEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andGreaterThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orGreaterThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orGreaterThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andLessThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orLessThan(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orLessThanOrEqualTo(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andNotIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orNotIn(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andMatches(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orMatches(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition andFindInSet(Fn<T, F> fn, ColumFunction columFunction);

    <T, F> FunctionCondition orFindInSet(Fn<T, F> fn, ColumFunction columFunction);

}
