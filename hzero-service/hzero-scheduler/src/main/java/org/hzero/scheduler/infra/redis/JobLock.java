package org.hzero.scheduler.infra.redis;

import java.util.concurrent.TimeUnit;

import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.scheduler.config.SchedulerConfiguration;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/06/20 10:35
 */
public class JobLock {

    private volatile static RedisHelper redisHelper;
    private volatile static SchedulerConfiguration configuration;

    private JobLock() {
    }

    private static RedisHelper getRedisHelper() {
        if (null == redisHelper) {
            synchronized (JobLock.class) {
                if (null == redisHelper) {
                    redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
                }
            }
        }
        return redisHelper;
    }

    private static SchedulerConfiguration getConfiguration() {
        if (null == configuration) {
            synchronized (JobLock.class) {
                if (null == configuration) {
                    configuration = ApplicationContextHelper.getContext().getBean(SchedulerConfiguration.class);
                }
            }
        }
        return configuration;
    }

    /**
     * 生成redis存储key
     *
     * @param jobId 任务ID
     * @return key
     */
    private static String getCacheKey(Long jobId) {
        return HZeroService.Scheduler.CODE + ":job-lock:" + jobId;
    }

    /**
     * 加锁
     *
     * @param jobId 任务Id
     */
    public static boolean addLock(Long jobId) {
        String key = getCacheKey(jobId);
        boolean flag = Boolean.TRUE.equals(getRedisHelper().strSetIfAbsent(key, "lock"));
        if (flag) {
            getRedisHelper().setExpire(key, getConfiguration().getLockTime(), TimeUnit.SECONDS);
        }
        return flag;
    }

    /**
     * 清除锁
     *
     * @param jobId 任务Id
     */
    public static void clearLock(Long jobId) {
        getRedisHelper().delKey(getCacheKey(jobId));
    }
}
