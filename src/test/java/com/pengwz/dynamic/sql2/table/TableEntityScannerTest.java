package com.pengwz.dynamic.sql2.table;

import com.pengwz.dynamic.sql2.anno.Table;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

class SchemaStructureScannerTest {
    @BeforeAll
    static void setUp() {
//        DataSourceUtils.scanAndInitDataSource("com.pengwz.dynamic.sql2", "");
    }

    @Test
    void findTableEntities() {
        List<String> list = Arrays.asList("com.pengwz.dynamic.sql2.table"
                , "com.pengwz.dynamic.sql2.entites");
        for (String s : list) {
            List<TableEntityMapping> tableEntities = SchemaStructureScanner.findTableEntities(s);
            Assertions.assertNotNull(tableEntities);
            System.out.println(tableEntities.size());
            tableEntities.forEach(System.out::println);
        }
    }

    @Test
    void matchBestDataSourceName() {
        //MysqlCustomDataSource#dataSource 设置为默认数据源
        String dbname = SchemaStructureScanner.matchBestDataSourceName(Aoo.class, null);
        Assertions.assertEquals("dataSource", dbname);
        String dbname2 = SchemaStructureScanner.matchBestDataSourceName(Boo.class, "testDB");
        Assertions.assertEquals("testDB", dbname2);
        try {
            SchemaStructureScanner.matchBestDataSourceName(Aoo.class, "testDB-1");
        } catch (Exception e) {
            //ignore
        }
    }

    @Test
    void getBestMatch() {
        String path1 = "com.pengwz";
        String path2 = "com.pengwz.dynamic.sql2.table";
        String path3 = "com.pengwz.dynamic.sql2";
        String bestMatch = SchemaStructureScanner.getBestMatch(Aoo.class.getCanonicalName(), new String[]{path1, path2, path3});
        System.out.println(bestMatch);
    }
}

@Table("aoo")
class Aoo {

}

@Table(value = "boo", dataSourceName = "testDB")
class Boo {

}