package org.hzero.websocket.redis;

import org.hzero.core.redis.RedisHelper;
import org.hzero.websocket.config.WebSocketConfig;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * websocket缓存父类
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/01 20:24
 */
public abstract class WebSocketRedis {

    private volatile static RedisHelper redisHelper;
    private volatile static WebSocketConfig config;

    protected WebSocketRedis() {
    }

    protected static RedisHelper getRedisHelper() {
        if (null == redisHelper) {
            synchronized (SessionRedis.class) {
                if (null == redisHelper) {
                    redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);
                }
            }
        }
        return redisHelper;
    }

    protected static WebSocketConfig getConfig() {
        if (null == config) {
            synchronized (SessionRedis.class) {
                if (null == config) {
                    config = ApplicationContextHelper.getContext().getBean(WebSocketConfig.class);
                }
            }
        }
        return config;
    }
}
