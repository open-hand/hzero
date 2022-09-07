package org.hzero.boot.imported.infra.redis;

import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 导入进度缓存
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/16 17:38
 */
public class AmountRedis {

    private AmountRedis() {
    }

    public static Integer getCount(RedisHelper redisHelper, String batch) {
        String result = redisHelper.strGet(getCountKey(batch));
        if (StringUtils.isNotBlank(result)) {
            return Integer.valueOf(result);
        }
        return 0;
    }

    public static Integer getReady(RedisHelper redisHelper, String batch) {
        String result = redisHelper.strGet(getReadyKey(batch));
        if (StringUtils.isNotBlank(result)) {
            return Integer.valueOf(result);
        }
        return 0;
    }

    private static String getCountKey(String batch) {
        return HZeroService.Import.CODE + ":count:" + batch;
    }

    private static String getReadyKey(String batch) {
        return HZeroService.Import.CODE + ":ready:" + batch;
    }


    public static void refreshCount(RedisHelper redisHelper, String batch, Integer count) {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        clear(redisHelper, batch);
        redisHelper.strSet(getCountKey(batch), String.valueOf(count), 1, TimeUnit.DAYS);
    }

    /**
     * 递增刷新
     */
    public static void refreshReady(RedisHelper redisHelper, String batch, Integer ready) {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        String key = getReadyKey(batch);
        boolean flag = redisHelper.hasKey(key);
        if (flag) {
            redisHelper.strIncrement(key, Long.valueOf(ready));
            redisHelper.setExpire(key, 1, TimeUnit.DAYS);
        } else {
            redisHelper.strSet(key, String.valueOf(ready), 1, TimeUnit.DAYS);
        }
    }

    /**
     * 总数刷新
     */
    public static void refresh(RedisHelper redisHelper, String batch, Integer readyCount) {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        String key = getReadyKey(batch);
        redisHelper.delKey(key);
        redisHelper.strSet(key, String.valueOf(readyCount), 1, TimeUnit.DAYS);
    }


    public static void clear(RedisHelper redisHelper, String batch) {
        if (redisHelper == null) {
            redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
        }
        redisHelper.delKey(getCountKey(batch));
        redisHelper.delKey(getReadyKey(batch));
    }
}
