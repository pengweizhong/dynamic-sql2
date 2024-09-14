package com.pengwz.dynamic.sql2.core.column;

import java.io.Serializable;

@FunctionalInterface
public interface IColumn extends Serializable {

    String getColumnName();

}
