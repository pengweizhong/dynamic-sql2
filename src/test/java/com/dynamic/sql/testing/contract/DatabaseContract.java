package com.dynamic.sql.testing.contract;

import com.dynamic.sql.core.SqlContext;
import com.dynamic.sql.testing.model.*;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * 各数据库共用的行为契约，使用固定业务样本验证框架的读写和关联能力。
 * <p>
 * 方言实现负责提供 SqlContext、连接配置与事务隔离；数据库专属类型放在对应方言测试中。
 */
public abstract class DatabaseContract {
    /**
     * 获取当前方言已初始化的 SQL 上下文，写入操作应由实现类在用例结束后回滚。
     */
    protected abstract SqlContext context();

    /**
     * 按固定主键读取样本，验证实体字段映射及不存在记录时的返回值。
     */
    @Test void readsSeedByPrimaryKey() {
        Account account = context().selectByPrimaryKey(Account.class, 100L);
        assertEquals("alice", account.getLoginName());
        assertEquals(0, new BigDecimal("100.2500").compareTo(account.getBalance()));
        assertEquals(LocalDateTime.of(2024,2,29,10,20,30,123456000), account.getCreatedAt());
        assertNull(context().selectByPrimaryKey(Account.class, -1L));
    }

    /**
     * 验证新增、查询、选择性更新和删除的完整闭环，包含需要参数绑定的特殊字符。
     */
    @Test void insertUpdateDeleteRoundTrip() {
        Department department = new Department();
        department.setId(90001L);
        department.setName("中文 O'Reilly % _ ?");
        assertEquals(1, context().insert(department));
        assertEquals(department.getName(), context().selectByPrimaryKey(Department.class, 90001L).getName());
        department.setName("updated");
        assertEquals(1, context().updateSelectiveByPrimaryKey(department));
        assertEquals("updated", context().selectByPrimaryKey(Department.class, 90001L).getName());
        assertEquals(1, context().deleteByPrimaryKey(Department.class, 90001L));
        assertNull(context().selectByPrimaryKey(Department.class, 90001L));
    }

    /**
     * 批量写入后核对实际记录数，再验证按多个主键删除的影响行数。
     */
    @Test void batchInsertAndDelete() {
        Department first = new Department(); first.setId(90002L); first.setName("first");
        Department second = new Department(); second.setId(90003L); second.setName("second");
        context().insertBatch(Arrays.asList(first, second));
        assertEquals(2, context().selectByPrimaryKey(Department.class, Arrays.asList(90002L,90003L)).size());
        assertEquals(2, context().deleteByPrimaryKey(Department.class, Arrays.asList(90002L,90003L)));
    }

    /**
     * DSL 条件中的输入不能改变查询逻辑，正常条件仍应准确返回匹配记录。
     */
    @Test void dslBindsHostileText() {
        assertTrue(context().select().allColumn().from(Account.class)
                .where(w -> w.andEqualTo(Account::getLoginName, "alice' OR 1=1 --"))
                .fetch(Account.class).toList().isEmpty());
        List<Account> accounts = context().select().allColumn().from(Account.class)
                .where(w -> w.andEqualTo(Account::getDepartmentId,20L)).fetch(Account.class).toList();
        Set<Long> ids = new HashSet<>();
        accounts.forEach(a -> ids.add(a.getId()));
        assertEquals(new HashSet<>(Arrays.asList(100L,101L)),ids);
    }

    /**
     * 创建时间相同时追加唯一主键排序，确保分页结果稳定。
     */
    @Test void stablePaginationUsesUniqueTieBreaker() {
        List<Account> page = context().select().allColumn().from(Account.class)
                .orderBy(Account::getCreatedAt).thenOrderBy(Account::getId)
                .limit(1,2).fetch(Account.class).toList();
        assertEquals(2,page.size());
        assertEquals(Long.valueOf(101),page.get(0).getId());
        assertEquals(Long.valueOf(102),page.get(1).getId());
    }

    /**
     * LEFT JOIN 应保留没有关联部门的账户，并允许通过右表 NULL 条件筛选。
     */
    @Test void leftJoinPreservesAccountsWithoutDepartment() {
        List<Account> accounts = context().select().column(Account::getId).from(Account.class)
                .leftJoin(Department.class,on -> on.andEqualTo(Account::getDepartmentId,Department::getId))
                .where(w -> w.andIsNull(Department::getId)).fetch(Account.class).toList();
        assertEquals(1,accounts.size());
        assertEquals(Long.valueOf(103),accounts.get(0).getId());
    }

    /**
     * 通过框架预编译写入再读取，检查数值、二进制和微秒时间是否完整保留。
     */
    @Test void typeValuesRoundTripThroughPreparedWrites() {
        TypeSample row = new TypeSample();
        row.setId(90010L); row.setSampleName("round-trip");
        row.setDecimalValue(new BigDecimal("1234567890.1234567890"));
        row.setLongValue(9007199254740993L);
        row.setVarcharValue("中文😀 O'Reilly");
        row.setBlobValue(new byte[]{0,39,92,(byte)255});
        row.setDatetimeValue(LocalDateTime.of(2024,2,29,10,20,30,123456000));
        row.setTimeValue(LocalTime.of(10,20,30,123456000));
        assertEquals(1,context().insertSelective(row));
        TypeSample fetched = context().selectByPrimaryKey(TypeSample.class,90010L);
        assertEquals(row.getDecimalValue(),fetched.getDecimalValue());
        assertEquals(row.getLongValue(),fetched.getLongValue());
        assertEquals(row.getVarcharValue(),fetched.getVarcharValue());
        assertArrayEquals(row.getBlobValue(),fetched.getBlobValue());
        assertEquals(row.getDatetimeValue(),fetched.getDatetimeValue());
        assertEquals(row.getTimeValue(),fetched.getTimeValue());
        assertEquals("default-value",fetched.getDefaultValue());
    }

    /**
     * 覆盖有符号整数上下界及高精度 DECIMAL，避免精度丢失或溢出。
     */
    @Test void numericBoundariesAreExact() {
        TypeSample min = context().selectByPrimaryKey(TypeSample.class,4L);
        TypeSample max = context().selectByPrimaryKey(TypeSample.class,5L);
        assertEquals(Long.valueOf(Long.MIN_VALUE),min.getLongValue());
        assertEquals(Long.valueOf(Long.MAX_VALUE),max.getLongValue());
        assertEquals(Integer.valueOf(Integer.MIN_VALUE),min.getIntValue());
        assertEquals(Integer.valueOf(Integer.MAX_VALUE),max.getIntValue());
        assertEquals(Short.valueOf(Short.MIN_VALUE),min.getSmallValue());
        assertEquals(Short.valueOf(Short.MAX_VALUE),max.getSmallValue());
        assertEquals(new BigDecimal("9999999999999999999999999999.9999999999"),max.getDecimalValue());
        assertEquals(max.getDecimalValue().negate(),min.getDecimalValue());
    }

    /**
     * 验证中文、emoji、零字节及时间微秒映射；浮点数按容差比较。
     */
    @Test void unicodeBinaryAndTemporalMapping() {
        TypeSample row = context().selectByPrimaryKey(TypeSample.class,3L);
        assertEquals("中文😀 O'Reilly 100% _ : ?",row.getVarcharValue());
        assertEquals("第一行\n第二行",row.getTextValue());
        assertArrayEquals(new byte[]{0,0x01,0x02,(byte)0xfe,(byte)0xff},row.getBlobValue());
        assertEquals(LocalDate.of(2024,2,29),row.getDateValue());
        assertEquals(LocalTime.of(23,59,59,123456000),row.getTimeValue());
        assertEquals(LocalDateTime.of(2024,2,29,23,59,59,123456000),row.getDatetimeValue());
        assertEquals(row.getDatetimeValue(),row.getTimestampValue());
        assertEquals(1.25f,row.getFloatValue(),0.00001f);
        assertEquals(1.23456789012345,row.getDoubleValue(),1e-12);
    }

    /**
     * 可空字段应保留 null，未显式写入的默认值应由数据库提供。
     */
    @Test void nullableFieldsAndDatabaseDefaults() {
        TypeSample row = context().selectByPrimaryKey(TypeSample.class,1L);
        assertNull(row.getLongValue()); assertNull(row.getVarcharValue());
        assertNull(row.getBlobValue()); assertNull(row.getTimestampValue());
        assertEquals("default-value",row.getDefaultValue());
    }

    /**
     * 核对自关联、一对一、多对多与多级关联的基数和金额汇总。
     */
    @Test void relationshipCardinality() {
        assertEquals(Long.valueOf(1),context().selectOne("SELECT COUNT(*) FROM ds_account WHERE department_id IS NULL",Long.class));
        assertEquals(Long.valueOf(2),context().selectOne("SELECT COUNT(*) FROM ds_account a LEFT JOIN ds_account_profile p ON p.account_id=a.id WHERE p.account_id IS NULL",Long.class));
        assertEquals(Long.valueOf(2),context().selectOne("SELECT COUNT(*) FROM ds_account_role WHERE account_id=100",Long.class));
        assertEquals(Long.valueOf(3),context().selectOne("SELECT COUNT(*) FROM ds_department WHERE parent_id=10",Long.class));
        assertEquals(new BigDecimal("50.7500"),context().selectOne("SELECT SUM(i.quantity*i.unit_price-i.discount_amount) FROM ds_purchase_order o JOIN ds_order_item i ON i.order_id=o.id WHERE o.account_id=100",BigDecimal.class));
    }
}
