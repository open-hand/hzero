package io.choerodon.mybatis.helper.snowflake;

import io.choerodon.core.exception.CommonException;
import org.hzero.core.util.SystemClock;
import org.springframework.util.Assert;

/**
 * Twitter Snowflake
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000
 * 第 1  位标识位：符号标识，由于ID一般是正数，第一位一般固定 0
 * 后 41 位时间戳：毫秒级，可以存储起始时间到当前时间的时间戳的差值，最多可以使用约 69 年，(1L &lt;&lt; 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 后 10 位机器位：可以部署1024个节点，包含 5 位数据中心ID（dataCenterId）和 5 位工作机器ID（workerId）
 * 后 12 位序列位：毫秒内的计算。12 位的计数顺序号支持每个节点，每毫秒生成 4096 个序号
 * 总 64 位：Long
 * 整体上按照时间自增排序，并且整个分布式系统内不会产生ID碰撞
 *
 * @author qingsheng.chen@hand-china.com
 */
public class SnowflakeHelper {
    /**
     * 时间错位数
     */
    private static final int BIT_TIMESTAMP = 41;
    /**
     * 序列位数
     */
    private static final int BIT_SEQUENCE = 12;
    /**
     * 数据中心位数
     */
    private static final int BIT_DATA_CENTER = 5;
    /**
     * 工作机器位数
     */
    private static final int BIT_WORKER = 5;
    /**
     * 总位数
     */
    private static final int BIT_SNOWFLAKE = BIT_TIMESTAMP + BIT_DATA_CENTER + BIT_WORKER + BIT_SEQUENCE;

    /**
     * 最大序列值
     */
    private static final long MAX_SEQUENCE = ~(-1L << BIT_SEQUENCE);
    /**
     * 最大数据中心值
     */
    private static final long MAX_DATA_CENTER = ~(-1L << BIT_DATA_CENTER);
    /**
     * 最大工作机器值
     */
    private static final long MAX_WORKER = ~(-1L << BIT_WORKER);

    /**
     * 时间戳左移位数
     */
    private static final long LEFT_TIMESTAMP = BIT_DATA_CENTER + BIT_WORKER + BIT_SEQUENCE;
    /**
     * 数据中心左移位数
     */
    private static final long LEFT_DATA_CENTER = BIT_WORKER + BIT_SEQUENCE;
    /**
     * 工作机器左移位数
     */
    private static final long LEFT_WORKER = BIT_SEQUENCE;

    /**
     * 雪花ID开始时间，可以从起始时间开始使用 69 年
     */
    private final long START_TIMESTAMP;
    /**
     * 数据中心ID
     */
    private final long DATA_CENTER_ID;
    /**
     * 工作机器ID
     */
    private final long WORKER_ID;
    /**
     * 上一次的时间戳
     */
    private long latestTimestamp = 0L;
    /**
     * 序列
     */
    private long sequence = 0L;

    private long leftWorker = LEFT_WORKER;

    private long leftDataCenter = LEFT_DATA_CENTER;

    private long leftTimestamp = LEFT_TIMESTAMP;

    public SnowflakeHelper(long startTimeStamp, long dataCenterId, long workerId) {
        if (dataCenterId < 0 || dataCenterId > MAX_DATA_CENTER) {
            throw new IllegalArgumentException("Data center id must in 0 ~ " + MAX_DATA_CENTER);
        }
        if (workerId < 0 || workerId > MAX_WORKER) {
            throw new IllegalArgumentException("Worker id must in 0 ~ " + MAX_WORKER);
        }
        this.START_TIMESTAMP = startTimeStamp;
        this.DATA_CENTER_ID = dataCenterId;
        this.WORKER_ID = workerId;
    }

    public SnowflakeHelper(long startTimeStamp, long dataCenterId, long workerId,
                           Integer bitTimestamp,
                           Integer bitDataCenterId,
                           Integer bitWorkerId,
                           Integer bitSequence) {
        this(startTimeStamp, dataCenterId, workerId);
        bitTimestamp = bitTimestamp != null ? bitTimestamp : BIT_TIMESTAMP;
        bitDataCenterId = bitDataCenterId != null ? bitDataCenterId : BIT_DATA_CENTER;
        bitWorkerId = bitWorkerId != null ? bitWorkerId : BIT_WORKER;
        bitSequence = bitSequence != null ? bitSequence : BIT_SEQUENCE;
        Assert.isTrue((bitTimestamp + bitDataCenterId + bitWorkerId + bitSequence) == BIT_SNOWFLAKE,
                String.format("[Snowflake] The total length of the snowflake ID must be 63 bit (timestamp %d, data center %d, worker %d, sequence %d).", bitTimestamp, bitDataCenterId, bitWorkerId, bitSequence));
        leftWorker = bitSequence;
        leftDataCenter = leftWorker + bitWorkerId;
        leftTimestamp = leftDataCenter + bitDataCenterId;
    }

    public synchronized long next() {
        long now = now();
        if (now < latestTimestamp) {
            throw new CommonException("Snowflake ID clock abnormal.");
        }
        if (now == latestTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                now = nextTime();
            }
        } else {
            sequence = 0L;
        }
        latestTimestamp = now;
        // 时间戳位
        return (now - START_TIMESTAMP) << leftTimestamp
                // 数据中心位
                | DATA_CENTER_ID << leftDataCenter
                // 工作机器位
                | WORKER_ID << leftWorker
                // 序列位
                | sequence;
    }

    private long nextTime() {
        long now = now();
        while (now <= latestTimestamp) {
            now = now();
        }
        return now;
    }

    private long now() {
        return SystemClock.now();
    }
}
