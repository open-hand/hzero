package org.hzero.core.redis;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * RedisProperties
 */
@ConfigurationProperties(prefix = HZeroRedisProperties.PREFIX)
public class HZeroRedisProperties {

    public static final String PREFIX = "hzero.redis";

    /**
     * 是否开启动态数据库切换 默认开启
     */
    private boolean dynamicDatabase = true;

    /**
     * 启用redis消息队列
     */
    private boolean redisQueue = true;

    /**
     * 队列消息默认db
     */
    private int queueDb = 1;

    /**
     * 消费间隔时间，单位秒
     */
    private int intervals = 5;

    public boolean isDynamicDatabase() {
        return dynamicDatabase;
    }

    public void setDynamicDatabase(boolean dynamicDatabase) {
        this.dynamicDatabase = dynamicDatabase;
    }

    public int getQueueDb() {
        return queueDb;
    }

    public void setQueueDb(int queueDb) {
        this.queueDb = queueDb;
    }

    public boolean isRedisQueue() {
        return redisQueue;
    }

    public HZeroRedisProperties setRedisQueue(boolean redisQueue) {
        this.redisQueue = redisQueue;
        return this;
    }

    public int getIntervals() {
        return intervals;
    }

    public HZeroRedisProperties setIntervals(int intervals) {
        this.intervals = intervals;
        return this;
    }
}
