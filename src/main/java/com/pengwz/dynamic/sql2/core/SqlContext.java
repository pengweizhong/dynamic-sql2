package com.pengwz.dynamic.sql2.core;

import com.pengwz.dynamic.sql2.core.dml.delete.DataDeleter;
import com.pengwz.dynamic.sql2.core.dml.insert.DataInserter;
import com.pengwz.dynamic.sql2.core.dml.select.AbstractColumnReference;
import com.pengwz.dynamic.sql2.core.dml.update.DataUpdater;

public interface SqlContext extends DataInserter, DataUpdater, DataDeleter {

    AbstractColumnReference select();

}
