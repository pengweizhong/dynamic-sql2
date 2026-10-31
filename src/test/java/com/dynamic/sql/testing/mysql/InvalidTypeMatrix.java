package com.dynamic.sql.testing.mysql;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.testing.model.MysqlTypeSample;
import com.dynamic.sql.testing.model.TypeSample;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Stream;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.function.Executable;
import static org.junit.jupiter.api.Assertions.*;


/** 严格模式下拒绝非法新增和更新，检查失败操作没有产生脏数据或截断原记录。 */
final class InvalidTypeMatrix {
    private InvalidTypeMatrix() { }

    static Stream<DynamicTest> cases(SqlContext context,Connection connection) {
        Object[][] inputs={
            {TypeSample.class,"ds_type_sample","varcharValue","ok",repeat(513),1406},
            {TypeSample.class,"ds_type_sample","charValue","ok",repeat(9),1406},
            {TypeSample.class,"ds_type_sample","varbinaryValue",new byte[]{1},new byte[257],1406},
            {TypeSample.class,"ds_type_sample","blobValue",new byte[]{1},new byte[65536],1406},
            {TypeSample.class,"ds_type_sample","decimalValue",BigDecimal.ONE,new BigDecimal("10000000000000000000000000000"),1264},
            {MysqlTypeSample.class,"ds_mysql_type_sample","unsignedTiny",1,-1,1264},
            {MysqlTypeSample.class,"ds_mysql_type_sample","unsignedInt",1L,4294967296L,1264},
            {MysqlTypeSample.class,"ds_mysql_type_sample","mediumValue",1,8388608,1264},
            {MysqlTypeSample.class,"ds_mysql_type_sample","tinyText","ok",repeat(256),1406},
            {MysqlTypeSample.class,"ds_mysql_type_sample","tinyBlob",new byte[]{1},new byte[256],1406},
            {MysqlTypeSample.class,"ds_mysql_type_sample","jsonValue","{}","{invalid",3140},
            {MysqlTypeSample.class,"ds_mysql_type_sample","enumValue","NEW","INVALID",1265},
            {MysqlTypeSample.class,"ds_mysql_type_sample","setValue","READ","INVALID",1265}
        };
        return Arrays.stream(inputs).map(c -> DynamicTest.dynamicTest("reject."+c[2],()->run(context,connection,c)));
    }

    private static void run(SqlContext context,Connection connection,Object[] c) throws Exception {
        Savepoint savepoint=connection.setSavepoint();
        try {
            Class<?> type=(Class<?>)c[0]; String table=(String)c[1],name=(String)c[2];
            Object row=type.newInstance(); set(row,"id",93000L);
            if (row instanceof TypeSample) set(row,"sampleName","invalid-matrix");
            set(row,name,c[4]);
            rejects((Integer)c[5],()->context.insertSelective(row));
            assertNull(context.selectByPrimaryKey(type,93000L));
            set(row,name,c[3]); assertEquals(1,context.insertSelective(row));
            String column=name.replaceAll("([A-Z])","_$1").toLowerCase(Locale.ROOT);
            String stored=read(connection,table,column);
            set(row,name,c[4]);
            rejects((Integer)c[5],()->context.updateSelectiveByPrimaryKey(row));
            assertEquals(stored,read(connection,table,column));
        } finally { connection.rollback(savepoint); connection.releaseSavepoint(savepoint); }
    }

    /** 检查真正的数据库错误码，避免把框架的其他异常误认为预期的输入拒绝。 */
    private static void rejects(int code,Executable operation) {
        RuntimeException error=assertThrows(RuntimeException.class,operation);
        Throwable cause=error;
        while (!(cause instanceof SQLException) && cause.getCause()!=null) cause=cause.getCause();
        assertInstanceOf(SQLException.class,cause);
        assertEquals(code,((SQLException)cause).getErrorCode());
    }
    private static String read(Connection c,String table,String column) throws Exception {
        try (Statement s=c.createStatement(); ResultSet r=s.executeQuery("SELECT "+column+" FROM "+table+" WHERE id=93000")) {
            assertTrue(r.next()); return r.getString(1);
        }
    }
    private static void set(Object row,String name,Object value) throws Exception {
        Field f=row.getClass().getDeclaredField(name); f.setAccessible(true); f.set(row,value);
    }
    private static String repeat(int length) {
        char[] value=new char[length]; Arrays.fill(value,'x'); return new String(value);
    }
}
