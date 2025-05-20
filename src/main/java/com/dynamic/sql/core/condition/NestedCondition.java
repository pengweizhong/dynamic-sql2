package com.dynamic.sql.core.condition;


import com.dynamic.sql.core.AbstractColumnReference;
import com.dynamic.sql.core.Fn;

import java.util.function.Consumer;

public interface NestedCondition<C extends NestedCondition<C>> extends Condition<C> {

    <T, F> C andEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orNotEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orGreaterThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orGreaterThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orLessThan(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orLessThanOrEqualTo(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C andNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    <T, F> C orNotIn(Fn<T, F> fn, Consumer<AbstractColumnReference> nestedSelect);

    C andExists(Consumer<AbstractColumnReference> nestedSelect);

    C orExists(Consumer<AbstractColumnReference> nestedSelect);

    C andNotExists(Consumer<AbstractColumnReference> nestedSelect);

    C orNotExists(Consumer<AbstractColumnReference> nestedSelect);

}
