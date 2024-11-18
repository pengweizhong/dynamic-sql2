package com.pengwz.dynamic.sql2.core.database;

import com.pengwz.dynamic.sql2.core.dml.delete.DeleteHandler;
import com.pengwz.dynamic.sql2.core.dml.insert.InsertHandler;
import com.pengwz.dynamic.sql2.core.dml.select.SelectHandler;
import com.pengwz.dynamic.sql2.core.dml.update.UpdateHandler;

public interface SqlExecutor extends SelectHandler, InsertHandler, DeleteHandler, UpdateHandler {

}