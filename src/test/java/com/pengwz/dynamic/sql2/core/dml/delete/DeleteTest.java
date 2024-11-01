package com.pengwz.dynamic.sql2.core.dml.delete;

import com.pengwz.dynamic.sql2.InitializingContext;
import org.junit.jupiter.api.Test;

class DeleteTest extends InitializingContext {

    @Test
    void delete() {
        int pkValue = 5041;
        sqlContext.deleteByPrimaryKey(pkValue);
    }
}