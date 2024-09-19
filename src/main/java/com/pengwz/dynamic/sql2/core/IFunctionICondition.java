package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.column.function.IColumFunction;

public interface IFunctionICondition extends ICondition {

    <T, F> IFunctionICondition andEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andNotEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orNotEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition andLengthEquals(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orLengthEquals(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andLengthGreaterThan(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orLengthGreaterThan(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andLengthLessThan(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orLengthLessThan(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andGreaterThan(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition orGreaterThan(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition andGreaterThanOrEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orGreaterThanOrEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition andLessThan(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition orLessThan(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition andLessThanOrEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);


    <T, F> ICondition orLessThanOrEqualTo(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andNotIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orNotIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andLike(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orLike(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andNotLike(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orNotLike(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andMatches(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orMatches(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andFindInSet(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andFindInSet(Fn<T, F> fn, IColumFunction iColumFunction, String separator);

    <T, F> ICondition orFindInSet(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orFindInSet(Fn<T, F> fn, IColumFunction iColumFunction, String separator);

    <T, F> ICondition andContains(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orContains(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andAnyIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orAnyIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition andAllIn(Fn<T, F> fn, IColumFunction iColumFunction);

    <T, F> ICondition orAllIn(Fn<T, F> fn, IColumFunction iColumFunction);

}
