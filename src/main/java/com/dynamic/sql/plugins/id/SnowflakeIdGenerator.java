package com.dynamic.sql.plugins.id;

import java.util.Objects;
import java.util.function.LongSupplier;

/** 经典非负雪花 ID：41 位毫秒、10 位节点、12 位序列；节点由部署方分配，禁止自动猜测。 */
public final class SnowflakeIdGenerator implements IdGenerator {
    //默认纪元为 2024-01-01 UTC；集群各节点必须使用相同纪元。
    public static final long DEFAULT_EPOCH=1704067200000L;
    private final long nodeId;
    private final long epoch;
    private final LongSupplier clock;
    private long lastTime=-1;
    private long sequence;

    public SnowflakeIdGenerator(long nodeId) { this(nodeId,DEFAULT_EPOCH,System::currentTimeMillis); }

    /** 时钟可注入，便于独立验证回拨、序列耗尽和纪元边界。 */
    public SnowflakeIdGenerator(long nodeId,long epoch,LongSupplier clock) {
        if(nodeId<0 || nodeId>1023 || epoch<0) throw new IllegalArgumentException("Invalid Snowflake node or epoch");
        this.nodeId=nodeId;this.epoch=epoch;this.clock=Objects.requireNonNull(clock,"Clock must not be null");
    }
    @Override public Long generate(IdGenerationContext context) { return nextId(); }

    /** 同实例串行分配，回拨明确失败；序列耗尽最多等十毫秒，防止停滞时钟无限阻塞。 */
    public synchronized long nextId() {
        long now=clock.getAsLong();
        validate(now);
        if(now<lastTime) throw new IllegalStateException("Clock moved backwards");
        if(now==lastTime && sequence==4095) {
            long deadline=System.nanoTime()+10000000L;
            do {
                if(System.nanoTime()>=deadline) throw new IllegalStateException("Snowflake sequence exhausted while clock stalled");
                if(Thread.currentThread().isInterrupted()) throw new IllegalStateException("Interrupted while waiting for Snowflake clock");
                Thread.yield();now=clock.getAsLong();validate(now);
                if(now<lastTime) throw new IllegalStateException("Clock moved backwards");
            } while(now==lastTime);
        }
        sequence=now==lastTime ? sequence+1 : 0;
        lastTime=now;
        return ((now-epoch)<<22) | (nodeId<<12) | sequence;
    }
    private void validate(long now) {
        if(now<epoch || now-epoch>((1L<<41)-1)) throw new IllegalStateException("Clock is outside Snowflake timestamp range");
    }
}
