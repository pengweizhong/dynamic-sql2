package com.dynamic.sql.testing.unit;

import com.dynamic.sql.anno.GeneratedValue;
import com.dynamic.sql.anno.Id;
import com.dynamic.sql.enums.GenerationType;
import com.dynamic.sql.table.ColumnMeta;
import com.dynamic.sql.table.TableUtils;
import org.junit.jupiter.api.Test;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import static org.junit.jupiter.api.Assertions.*;

/** 主键注解元数据独立回归，不要求初始化数据源或连接 Oracle。 */
class GeneratedValueTest {
    static class Sample {
        @Id @GeneratedValue Long defaultId;
        @Id @GeneratedValue(strategy=GenerationType.AUTO) Long explicitId;
        @Id @GeneratedValue(strategy=GenerationType.SEQUENCE,sequenceName="seq_sample_id") Long sequenceId;
        @GeneratedValue(strategy=GenerationType.SEQUENCE,sequenceName="ignored") Long ordinary;
        @Id @GeneratedValue(strategy=GenerationType.SNOWFLAKE,generator="worker") Long snowflake;
        @Id @GeneratedValue(strategy=GenerationType.CUSTOM,generator="business") Long custom;
    }

    /** 默认及显式 AUTO 都表示数据库生成主键，序列名不应被误填。 */
    @Test void autoStrategyIsAlreadyAvailable() throws Exception {
        assertEquals(GenerationType.AUTO,column("defaultId").getGeneratedStrategy().getStrategy());
        assertEquals(GenerationType.AUTO,column("explicitId").getGeneratedStrategy().getStrategy());
        assertNull(column("defaultId").getGeneratedStrategy().getSequenceName());
    }

    /** 必须读取注解配置的序列名，不能把尚未赋值的元数据复制给自身。 */
    @Test void preservesConfiguredSequenceName() throws Exception {
        ColumnMeta meta=column("sequenceId");
        assertEquals(GenerationType.SEQUENCE,meta.getGeneratedStrategy().getStrategy());
        assertEquals("seq_sample_id",meta.getGeneratedStrategy().getSequenceName());
    }

    /** 普通字段上的 GeneratedValue 不能启用主键生成。 */
    @Test void onlyPrimaryKeyUsesGeneration() throws Exception {
        assertNull(column("ordinary").getGeneratedStrategy());
    }

    /** 应用生成器名称与策略必须保存在元数据，不能与序列名混用。 */
    @Test void preservesApplicationGeneratorConfiguration() throws Exception {
        assertEquals(GenerationType.SNOWFLAKE,column("snowflake").getGeneratedStrategy().getStrategy());
        assertEquals("worker",column("snowflake").getGeneratedStrategy().getGenerator());
        assertEquals("business",column("custom").getGeneratedStrategy().getGenerator());
    }

    /** 直接隔离列解析步骤，避免静态数据源注册影响元数据断言。 */
    private ColumnMeta column(String name) throws Exception {
        Field field=Sample.class.getDeclaredField(name);
        Method parse=TableUtils.class.getDeclaredMethod("parseTableColumn",String.class,Class.class,Field.class);
        parse.setAccessible(true);
        Object symbol=parse.invoke(null,null,Sample.class,field);
        Method get=symbol.getClass().getDeclaredMethod("getColumnMeta");get.setAccessible(true);
        return (ColumnMeta)get.invoke(symbol);
    }
}
