package org.hzero.boot.scheduler.infra.tool;

import org.apache.commons.lang3.StringUtils;
import org.hzero.boot.scheduler.api.dto.JobProgress;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.springframework.stereotype.Component;

/**
 * 任务进度缓存
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/29 14:55
 */
@Component
public class ProgressCache {

    /**
     * 生成redis存储key
     *
     * @param logId 日志Id
     * @return key
     */
    private String getCacheKey(Long logId) {
        return HZeroService.Scheduler.CODE + ":job-progress:" + logId;
    }

    /**
     * 刷新缓存
     *
     * @param logId    日志Id
     * @param progress 任务进度
     */
    public void refreshCache(Long logId, Integer progress, String message, RedisHelper redisHelper) {
        JobProgress jobProgress = new JobProgress().setMessage(message).setProgress(progress);
        redisHelper.strSet(getCacheKey(logId), redisHelper.toJson(jobProgress));
        // 缓存一天失效
        redisHelper.setExpire(getCacheKey(logId));
    }

    /**
     * 查询缓存(调度服务使用)
     *
     * @param logId 日志Id
     */
    public JobProgress getCache(Long logId, RedisHelper redisHelper) {
        String str = redisHelper.strGet(getCacheKey(logId));
        if (StringUtils.isBlank(str)) {
            return null;
        } else {
            return redisHelper.fromJson(str, JobProgress.class);
        }
    }

    /**
     * 清除缓存
     *
     * @param logId 日志Id
     */
    public void clearRedisCache(Long logId, RedisHelper redisHelper) {
        redisHelper.delKey(getCacheKey(logId));
    }
}
