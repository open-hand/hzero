package org.hzero.websocket.redis;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hzero.core.redis.RedisHelper;
import org.hzero.websocket.constant.WebSocketConstant;

/**
 * 存储服务节点与该节点的sessionId
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/01 20:49
 */
public class BrokerSessionRedis extends WebSocketRedis {

    /**
     * 生成redis存储key
     *
     * @param brokerId 节点Id
     * @return key
     */
    private static String getCacheKey(String brokerId) {
        return WebSocketConstant.REDIS_KEY + ":broker-sessions:" + brokerId;
    }

    /**
     * 添加缓存
     *
     * @param brokerId  节点Id
     * @param sessionId sessionId
     */
    public static void addCache(String brokerId, String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.hshPut(getCacheKey(brokerId), sessionId, StringUtils.EMPTY);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param brokerId  节点Id
     * @param sessionId sessionId
     */
    public static void clearCache(String brokerId, String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.hshDelete(getCacheKey(brokerId), sessionId);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param brokerId  节点Id
     */
    public static void clearCache(String brokerId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.delKey(getCacheKey(brokerId));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 查询缓存
     *
     * @param brokerId 节点Id
     */
    public static List<String> getSessionIds(String brokerId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        Set<String> set;
        try {
            set = ObjectUtils.defaultIfNull(redisHelper.hshKeys(getCacheKey(brokerId)), new HashSet<>());
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return new ArrayList<>(set);
    }
}
