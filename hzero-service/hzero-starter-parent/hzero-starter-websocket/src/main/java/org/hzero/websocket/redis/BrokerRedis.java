package org.hzero.websocket.redis;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.hzero.core.redis.RedisHelper;
import org.hzero.websocket.constant.WebSocketConstant;

/**
 * 客户端心跳缓存
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/30 15:17
 */
public class BrokerRedis extends WebSocketRedis {

    /**
     * 生成redis存储key
     *
     * @param brokerId 服务Id
     * @return key
     */
    private static String getCacheKey(String brokerId) {
        return WebSocketConstant.REDIS_KEY + ":socket-nodes:" + brokerId;
    }

    /**
     * 刷新缓存
     *
     * @param brokerId 客户端唯一标识
     */
    public static void refreshCache(String brokerId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.strSet(getCacheKey(brokerId), WebSocketConstant.ALIVE, 15, TimeUnit.SECONDS);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 客户端是否下线
     *
     * @param brokerId 客户端唯一标识
     */
    public static boolean isAlive(String brokerId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        String result;
        try {
            result = redisHelper.strGet(getCacheKey(brokerId));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return Objects.equals(result, WebSocketConstant.ALIVE);
    }
}
