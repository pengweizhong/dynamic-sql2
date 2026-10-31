package com.dynamic.sql.testing.unit;

import com.dynamic.sql.plugins.id.*;
import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/** 雪花算法使用可控时钟验证边界，不依赖机器执行速度或真实时钟回拨。 */
class SnowflakeIdTest {
    /** 拆分位布局，确认节点与同毫秒序列不会相互覆盖。 */
    @Test void encodesTimestampNodeAndSequence() {
        SnowflakeIdGenerator generator=new SnowflakeIdGenerator(1023,1000,()->1001);
        long first=generator.nextId(),second=generator.nextId();
        assertEquals(1,first>>>22);assertEquals(1023,(first>>>12)&1023);
        assertEquals(0,first&4095);assertEquals(1,second&4095);assertTrue(second>first);
    }
    /** 多线程共享实例必须产生互不重复的 ID。 */
    @Test void concurrentCallsAreUnique() throws Exception {
        SnowflakeIdGenerator generator=new SnowflakeIdGenerator(7);
        ExecutorService pool=Executors.newFixedThreadPool(4);
        try {
            List<Future<List<Long>>> futures=new ArrayList<>();
            for(int t=0;t<4;t++) futures.add(pool.submit(()->{
                List<Long> ids=new ArrayList<>();for(int i=0;i<1000;i++) ids.add(generator.nextId());return ids;
            }));
            Set<Long> unique=new HashSet<>();
            for(Future<List<Long>> future:futures) for(Long id:future.get(10,TimeUnit.SECONDS)) assertTrue(unique.add(id));
            assertEquals(4000,unique.size());
        } finally { pool.shutdownNow(); }
    }
    /** 回拨后立即拒绝，时钟追平后不能重复发出以前的序列。 */
    @Test void rejectsClockRollbackAndRecoversSafely() {
        AtomicLong clock=new AtomicLong(1001);
        SnowflakeIdGenerator generator=new SnowflakeIdGenerator(1,1000,clock::get);
        long first=generator.nextId();clock.set(1000);
        assertThrows(IllegalStateException.class,generator::nextId);
        clock.set(1001);assertTrue(generator.nextId()>first);
    }
    /** 停滞时钟达到每毫秒 4096 个 ID 后明确失败，而不是无限等待。 */
    @Test void boundsSequenceExhaustion() {
        AtomicLong clock=new AtomicLong(1001);
        SnowflakeIdGenerator generator=new SnowflakeIdGenerator(1,1000,clock::get);
        for(int i=0;i<4096;i++) generator.nextId();
        assertThrows(IllegalStateException.class,generator::nextId);
        clock.incrementAndGet();assertEquals(0,generator.nextId()&4095);
    }
    @Test void validatesNodeAndTimestampRange() {
        assertThrows(IllegalArgumentException.class,()->new SnowflakeIdGenerator(1024));
        assertThrows(IllegalStateException.class,()->new SnowflakeIdGenerator(0,1000,()->999).nextId());
        assertThrows(IllegalStateException.class,()->new SnowflakeIdGenerator(0,0,()->1L<<41).nextId());
    }
    /** 不同节点同毫秒不冲突，最大有效时间和序列仍不会占用符号位。 */
    @Test void isolatesNodesAndPreservesPositiveRange() {
        long left=new SnowflakeIdGenerator(1,0,()->1).nextId();
        long right=new SnowflakeIdGenerator(2,0,()->1).nextId();
        assertNotEquals(left,right);
        SnowflakeIdGenerator maximum=new SnowflakeIdGenerator(1023,0,()->(1L<<41)-1);
        long last=0;
        for(int i=0;i<4096;i++) last=maximum.nextId();
        assertEquals(Long.MAX_VALUE,last);
    }

    /** 注册表禁止覆盖，防止在线重建实例导致序列回退。 */
    @Test void registrationIsExplicitAndNonOverwriting() {
        assertThrows(IllegalStateException.class,()->IdGenerators.get("missing-unit-generator"));
        IdGenerators.register("unit-custom",context->42L);
        assertEquals(42L,IdGenerators.get("unit-custom").generate(null));
        assertThrows(IllegalStateException.class,()->IdGenerators.register("unit-custom",context->43L));
    }
}
