package com.dynamic.sql.core.column.function.table;

import com.dynamic.sql.InitializingContext;
import com.dynamic.sql.core.Version;
import com.dynamic.sql.core.column.function.json.JsonUnquote;
import com.dynamic.sql.entites.User;
import com.dynamic.sql.enums.SqlDialect;
import org.junit.jupiter.api.Test;

class AbstractTableFunctionTest extends InitializingContext {
    @Test
    void test() {
        JsonTable jsonTable = new JsonTable(new JsonUnquote(User::getName), "$.",
                //COLUMNS (
                //    product_name VARCHAR(150) PATH '$.product' DEFAULT 'Unknown' ON ERROR DEFAULT 'None' ON EMPTY,
                //    price DECIMAL(10, 2) PATH '$.price' DEFAULT 0.00 ON ERROR DEFAULT 0.99 ON EMPTY
                //)
                JsonTable.JsonColumn.builder().column("product_name").dataType("Varchar(32)").jsonPath("$.product").defaultValue("Unknown").on().error().defaultValue("None").on().empty().build(),
                JsonTable.JsonColumn.builder().column("price").dataType("DECIMAL(10, 2)").jsonPath("$.price").defaultValue(0.00).on().error().defaultValue(0.99).on().empty().build()

        );
        System.out.println(jsonTable.getFunctionToString(SqlDialect.MYSQL, new Version(1, 1, 1),null));
    }
}