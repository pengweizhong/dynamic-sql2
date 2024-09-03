package com.pengwz.dynamic.sql2.core;

import java.io.Serializable;
import java.util.function.Function;

@FunctionalInterface
public interface Fn<T, R> extends Function<T, R>, Serializable {


}