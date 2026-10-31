package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.testing.model.TypeSample;
import com.dynamic.sql.testing.model.MysqlTypeSample;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.DynamicTest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.*;
import java.time.*;
import java.util.*;
import java.util.stream.Stream;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 按字段逐项执行框架 CRUD，再用 JDBC 独立核对，避免框架写入和读取的错误互相抵消。
 * <p>
 * 每个动态用例使用保存点，即使断言失败也清理自身数据，不影响其他字段的测试。
 */
final class TypeCrudMatrix {
    private TypeCrudMatrix() { }

    /**
     * 定义每个字段的插入值和更新值，包含数值上下界、特殊字符与微秒时间。
     */
    static Stream<DynamicTest> cases(SqlContext context, Connection connection) {
        List<ColumnCase> cases = new ArrayList<>();
        add(cases, "smallValue", (short)-32768, (short)32767);
        add(cases, "intValue", Integer.MIN_VALUE, Integer.MAX_VALUE);
        add(cases, "longValue", Long.MIN_VALUE, Long.MAX_VALUE);
        add(cases, "decimalValue", new BigDecimal("-9999999999999999999999999999.9999999999"), new BigDecimal("9999999999999999999999999999.9999999999"));
        add(cases, "floatValue", -1.25f, 123.5f);
        add(cases, "doubleValue", -1.23456789012345, 98765.4321012345);
        add(cases, "flagValue", (short)0, (short)1);
        add(cases, "charValue", "中文", "ABCDEFGH");
        add(cases, "varcharValue", "中文😀 O'Reilly % _ ?", repeat("界",512));
        add(cases, "textValue", "first\n第二行", repeat("中文😀",1000));
        add(cases, "binaryValue", new byte[]{0,1,2,3,4,5,6,7}, new byte[]{-1,-2,-3,-4,-5,-6,-7,-8});
        add(cases, "varbinaryValue", new byte[0], bytes(256));
        add(cases, "blobValue", new byte[0], bytes(65535));
        add(cases, "dateValue", LocalDate.of(1000,1,1), LocalDate.of(9999,12,31));
        add(cases, "timeValue", LocalTime.MIDNIGHT, LocalTime.of(23,59,59,999999000));
        add(cases, "datetimeValue", LocalDateTime.of(1000,1,1,0,0), LocalDateTime.of(9999,12,31,23,59,59,999999000));
        add(cases, "timestampValue", LocalDateTime.of(2000,1,1,0,0), LocalDateTime.of(2030,12,31,23,59,59,123456000));
        mysql(cases, "tinyValue", (byte)-128, (byte)127);
        mysql(cases, "tinyBoolean", 0, 2);
        mysql(cases, "mediumValue", -8388608, 8388607);
        mysql(cases, "unsignedTiny", 0, 255);
        mysql(cases, "unsignedSmall", 0, 65535);
        mysql(cases, "unsignedMedium", 0, 16777215);
        mysql(cases, "unsignedInt", 0L, 4294967295L);
        mysql(cases, "unsignedBig", BigInteger.ZERO, new BigInteger("18446744073709551615"));
        mysql(cases, "bitFlag", 0, 1);
        mysql(cases, "bitMask", new byte[]{0}, new byte[]{(byte)170});
        mysql(cases, "bitWide", new byte[8], new byte[]{-1,-1,-1,-1,-1,-1,-1,-1});
        mysql(cases, "yearValue", Year.of(1901), Year.of(2155));
        mysql(cases, "durationValue", "-25:01:02.123456", "838:59:59");
        mysql(cases, "enumValue", "NEW", "CLOSED");
        mysql(cases, "setValue", "", "READ,WRITE,ADMIN");
        mysql(cases, "jsonValue", "{\"text\":\"中文\",\"n\":null}", "[true,123,\"😀\"]");
        mysql(cases, "tinyText", "", repeat("a",255));
        mysql(cases, "mediumText", "", repeat("中",30000));
        mysql(cases, "longText", "", repeat("文",30000));
        mysql(cases, "tinyBlob", new byte[0], bytes(255));
        mysql(cases, "mediumBlob", new byte[0], bytes(70000));
        mysql(cases, "longBlob", new byte[0], bytes(70000));
        mysql(cases, "baseAmount", new BigDecimal("-12.3456"), new BigDecimal("999999999999999.9999"));
        return cases.stream().map(c -> DynamicTest.dynamicTest(c.table + "." + c.column,
                () -> verifyCrud(context,connection,c)));
    }

    /**
     * 每个字段分别通过 insertBatch 与 insertMultiple 写入两行，之后独立核对并批量删除。
     */
    static Stream<DynamicTest> batchCases(SqlContext context, Connection connection) {
        List<ColumnCase> cases=new ArrayList<>();
        //复用字段矩阵中的相同类型和值，确保批量写入测试不只覆盖少数代表列。
        cases(cases);
        return cases.stream().map(c -> DynamicTest.dynamicTest("batch."+c.table+"."+c.column,
                () -> verifyBatch(context,connection,c)));
    }

    /**
     * 两种批量 API 使用不同主键，逐条核对真实列值和实体映射，最后确认删除结果。
     */
    private static void verifyBatch(SqlContext context, Connection connection, ColumnCase c) throws Exception {
        Savepoint savepoint=connection.setSavepoint();
        try {
            for(boolean multiple:new boolean[]{false,true}) {
                long firstId=multiple?91201L:91101L;
                Object first=batchEntity(c,firstId),second=batchEntity(c,firstId+1);
                int affected=multiple?context.insertMultiple(Arrays.asList(first,second))
                        :context.insertBatch(Arrays.asList(first,second));
                assertEquals(2,affected);
                verify(context,connection,c,c.initial,firstId);
                verify(context,connection,c,c.initial,firstId+1);
                assertEquals(2,context.deleteByPrimaryKey(c.entity,Arrays.asList(firstId,firstId+1)));
                try(Statement statement=connection.createStatement();ResultSet rows=statement.executeQuery(
                        "SELECT COUNT(*) FROM "+c.table+" WHERE id IN ("+firstId+","+(firstId+1)+")")) {
                    assertTrue(rows.next()); assertEquals(0,rows.getInt(1));
                }
            }
        } finally {
            connection.rollback(savepoint);
            connection.releaseSavepoint(savepoint);
        }
    }

    /**
     * 创建仅包含主键、必填识别列和待测字段的实体，其他列走数据库默认值或 NULL。
     */
    private static Object batchEntity(ColumnCase c,long id) throws Exception {
        Object row=c.entity.newInstance(); set(row,"id",id);
        if(row instanceof TypeSample) {
            set(row,"sampleName","batch-"+id);
            set(row,"defaultValue","default-value");
        }
        set(row,c.field,c.initial);
        return row;
    }

    private static void cases(List<ColumnCase> cases) {
        add(cases, "smallValue", (short)-32768, (short)32767);
        add(cases, "intValue", Integer.MIN_VALUE, Integer.MAX_VALUE);
        add(cases, "longValue", Long.MIN_VALUE, Long.MAX_VALUE);
        add(cases, "decimalValue", new BigDecimal("-9999999999999999999999999999.9999999999"), new BigDecimal("9999999999999999999999999999.9999999999"));
        add(cases, "floatValue", -1.25f, 123.5f); add(cases, "doubleValue", -1.23456789012345, 98765.4321012345);
        add(cases, "flagValue", (short)0, (short)1); add(cases, "charValue", "中文", "ABCDEFGH");
        add(cases, "varcharValue", "中文😀 O'Reilly % _ ?", repeat("界",512));
        add(cases, "textValue", "first\n第二行", repeat("中文😀",1000));
        add(cases, "binaryValue", new byte[]{0,1,2,3,4,5,6,7}, new byte[]{-1,-2,-3,-4,-5,-6,-7,-8});
        add(cases, "varbinaryValue", new byte[0], bytes(256)); add(cases, "blobValue", new byte[0], bytes(65535));
        add(cases, "dateValue", LocalDate.of(1000,1,1), LocalDate.of(9999,12,31));
        add(cases, "timeValue", LocalTime.MIDNIGHT, LocalTime.of(23,59,59,999999000));
        add(cases, "datetimeValue", LocalDateTime.of(1000,1,1,0,0), LocalDateTime.of(9999,12,31,23,59,59,999999000));
        add(cases, "timestampValue", LocalDateTime.of(2000,1,1,0,0), LocalDateTime.of(2030,12,31,23,59,59,123456000));
        mysql(cases,"tinyValue",(byte)-128,(byte)127); mysql(cases,"tinyBoolean",0,2);
        mysql(cases,"mediumValue",-8388608,8388607); mysql(cases,"unsignedTiny",0,255);
        mysql(cases,"unsignedSmall",0,65535); mysql(cases,"unsignedMedium",0,16777215);
        mysql(cases,"unsignedInt",0L,4294967295L); mysql(cases,"unsignedBig",BigInteger.ZERO,new BigInteger("18446744073709551615"));
        mysql(cases,"bitFlag",0,1); mysql(cases,"bitMask",new byte[]{0},new byte[]{(byte)170});
        mysql(cases,"bitWide",new byte[8],new byte[]{-1,-1,-1,-1,-1,-1,-1,-1});
        mysql(cases,"yearValue",Year.of(1901),Year.of(2155)); mysql(cases,"durationValue","-25:01:02.123456","838:59:59");
        mysql(cases,"enumValue","NEW","CLOSED"); mysql(cases,"setValue","","READ,WRITE,ADMIN");
        mysql(cases,"jsonValue","{\"text\":\"中文\",\"n\":null}","[true,123,\"😀\"]");
        mysql(cases,"tinyText","",repeat("a",255)); mysql(cases,"mediumText","",repeat("中",30000));
        mysql(cases,"longText","",repeat("文",30000)); mysql(cases,"tinyBlob",new byte[0],bytes(255));
        mysql(cases,"mediumBlob",new byte[0],bytes(70000)); mysql(cases,"longBlob",new byte[0],bytes(70000));
        mysql(cases,"baseAmount",new BigDecimal("-12.3456"),new BigDecimal("999999999999999.9999"));
    }

    /**
     * 新增、读取、选择性更新、更新为 NULL、删除均通过框架执行；每一步独立读取数据库确认。
     */
    private static void verifyCrud(SqlContext context, Connection connection, ColumnCase c) throws Exception {
        Savepoint savepoint = connection.setSavepoint();
        try {
            Object row = c.entity.newInstance();
            set(row,"id",91000L);
            if (row instanceof TypeSample) {
                set(row,"sampleName","crud-matrix");
                set(row,"defaultValue","default-value");
            }
            set(row,c.field,c.initial);
            assertEquals(1,context.insertSelective(row));
            verify(context,connection,c,c.initial);
            set(row,c.field,c.updated);
            assertEquals(1,context.updateSelectiveByPrimaryKey(row));
            verify(context,connection,c,c.updated);
            //全量更新必须显式写入 NULL，不能沿用选择性更新跳过空字段的行为。
            set(row,c.field,null);
            assertEquals(1,context.updateByPrimaryKey(row));
            verify(context,connection,c,null);
            assertEquals(1,context.deleteByPrimaryKey(c.entity,91000L));
            assertNull(context.selectByPrimaryKey(c.entity,91000L));
            try (Statement s=connection.createStatement(); ResultSet r=s.executeQuery("SELECT COUNT(*) FROM " + c.table + " WHERE id=91000")) {
                assertTrue(r.next()); assertEquals(0,r.getInt(1));
            }
        } finally {
            connection.rollback(savepoint);
            connection.releaseSavepoint(savepoint);
        }
    }

    /**
     * JDBC 按目标字段读取真实值，并单独核对框架实体映射。
     * 表名与列名仅来自本类固定测试清单，不接收外部输入。
     */
    private static void verify(SqlContext context, Connection connection, ColumnCase c, Object expected) throws Exception {
        verify(context,connection,c,expected,91000L);
    }

    private static void verify(SqlContext context,Connection connection,ColumnCase c,Object expected,long id) throws Exception {
        //负时长的 JDBC getString 存在二进制协议转换缺陷，先由数据库转为字符再独立核对真实落库值。
        String projection = c.field.equals("durationValue") ? "CAST(" + c.column + " AS CHAR)" : c.column;
        try (Statement s=connection.createStatement(); ResultSet r=s.executeQuery("SELECT " + projection + " FROM " + c.table + " WHERE id="+id)) {
            assertTrue(r.next());
            Object actual;
            if (expected == null) actual=r.getObject(1);
            else if (expected instanceof byte[]) actual=r.getBytes(1);
            else if (expected instanceof LocalDate) actual=r.getObject(1,LocalDate.class);
            else if (expected instanceof LocalTime) actual=r.getObject(1,LocalTime.class);
            else if (expected instanceof LocalDateTime) actual=r.getObject(1,LocalDateTime.class);
            else if (expected instanceof Year) actual=Year.of(r.getInt(1));
            else if (expected instanceof Number) actual=r.getBigDecimal(1);
            else actual=r.getString(1);
            equalValue(c,expected,actual);
        }
        Object fetched=context.selectByPrimaryKey(c.entity,id);
        assertNotNull(fetched);
        Field field=c.entity.getDeclaredField(c.field); field.setAccessible(true);
        equalValue(c,expected,field.get(fetched));
        if (c.field.equals("baseAmount")) {
            try (Statement s=connection.createStatement(); ResultSet r=s.executeQuery("SELECT doubled_amount FROM ds_mysql_type_sample WHERE id="+id)) {
                assertTrue(r.next());
                if (expected == null) assertNull(r.getBigDecimal(1));
                else assertEquals(0,((BigDecimal)expected).multiply(new BigDecimal("2")).compareTo(r.getBigDecimal(1)));
            }
        }
    }

    /**
     * 数值按精确值或浮点容差比较；JSON 按结构比较，避免依赖 MySQL 的空格和键排序。
     */
    private static void equalValue(ColumnCase c, Object expected, Object actual) {
        if (expected == null) { assertNull(actual,c.column); return; }
        assertNotNull(actual,c.column);
        if (expected instanceof byte[]) assertArrayEquals((byte[])expected,(byte[])actual,c.column);
        else if (expected instanceof Float || expected instanceof Double) assertEquals(((Number)expected).doubleValue(),((Number)actual).doubleValue(),Math.abs(((Number)expected).doubleValue())*1e-6,c.column);
        else if (expected instanceof Number) assertEquals(0,new BigDecimal(expected.toString()).compareTo(new BigDecimal(actual.toString())),c.column);
        else if (c.field.equals("jsonValue")) assertEquals(JsonParser.parseString(expected.toString()),JsonParser.parseString(actual.toString()));
        else if (c.field.equals("durationValue")) assertEquals(expected.toString(),actual.toString().replaceFirst("\\.0+$", ""));
        else assertEquals(expected,actual,c.column);
    }

    //反射只用于让同一 CRUD 契约覆盖不同字段，框架仍通过真实实体元数据生成 SQL。
    private static void set(Object row,String name,Object value) throws Exception {
        Field field=row.getClass().getDeclaredField(name); field.setAccessible(true); field.set(row,value);
    }
    private static void add(List<ColumnCase> cases,String field,Object initial,Object updated) {
        cases.add(new ColumnCase(TypeSample.class,"ds_type_sample",field,initial,updated));
    }
    private static void mysql(List<ColumnCase> cases,String field,Object initial,Object updated) {
        cases.add(new ColumnCase(MysqlTypeSample.class,"ds_mysql_type_sample",field,initial,updated));
    }
    //按固定模式构造内容，既包含零字节也包含高位字节，便于精确比较。
    private static byte[] bytes(int length) {
        byte[] bytes=new byte[length]; for (int i=0;i<length;i++) bytes[i]=(byte)i; return bytes;
    }
    private static String repeat(String text,int count) {
        StringBuilder result=new StringBuilder(); for(int i=0;i<count;i++) result.append(text); return result.toString();
    }
    /** 单个字段的两组数据，用于新增和更新阶段。 */
    private static final class ColumnCase {
        final Class<?> entity;
        final String table,field,column;
        final Object initial,updated;
        ColumnCase(Class<?> entity,String table,String field,Object initial,Object updated) {
            this.entity=entity; this.table=table; this.field=field;
            this.column=field.replaceAll("([A-Z])","_$1").toLowerCase(Locale.ROOT);
            this.initial=initial; this.updated=updated;
        }
    }
}
