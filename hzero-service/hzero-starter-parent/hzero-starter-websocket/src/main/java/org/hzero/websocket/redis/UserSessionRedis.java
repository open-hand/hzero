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
 * 存储用户与用户所属的sessionId
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/01 20:49
 */
public class UserSessionRedis extends WebSocketRedis {

    /**
     * 生成redis存储key
     *
     * @param userId 用户Id
     * @return key
     */
    private static String getCacheKey(Long userId) {
        return WebSocketConstant.REDIS_KEY + ":user-sessions:" + userId;
    }

    /**
     * 添加缓存
     *
     * @param userId    用户Id
     * @param sessionId sessionId
     */
    public static void addCache(Long userId, String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.hshPut(getCacheKey(userId), sessionId, StringUtils.EMPTY);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param userId    用户Id
     * @param sessionId sessionId
     */
    public static void clearCache(Long userId, String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.hshDelete(getCacheKey(userId), sessionId);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 查询缓存
     *
     * @param userId 用户Id
     */
    public static List<String> getSessionIds(Long userId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        Set<String> set;
        try {
            set = ObjectUtils.defaultIfNull(redisHelper.hshKeys(getCacheKey(userId)), new HashSet<>());
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return new ArrayList<>(set);
    }
}
