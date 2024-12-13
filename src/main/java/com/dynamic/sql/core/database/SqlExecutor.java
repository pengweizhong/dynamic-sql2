package com.dynamic.sql.core.database;


import com.dynamic.sql.core.dml.delete.DeleteHandler;
import com.dynamic.sql.core.dml.insert.InsertHandler;
import com.dynamic.sql.core.dml.select.SelectHandler;
import com.dynamic.sql.core.dml.update.UpdateHandler;

public interface SqlExecutor extends SelectHandler, InsertHandler, DeleteHandler, UpdateHandler {

}