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
 * 存储分组与分组所属的sessionId
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/02 9:46
 */
public class GroupSessionRedis extends WebSocketRedis {

    /**
     * 生成redis存储key
     *
     * @param group 分组
     * @return key
     */
    private static String getCacheKey(String group) {
        return WebSocketConstant.REDIS_KEY + ":group-sessions:" + group;
    }

    /**
     * 添加缓存
     *
     * @param group     分组
     * @param sessionId sessionId
     */
    public static void addCache(String group, String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.hshPut(getCacheKey(group), sessionId, StringUtils.EMPTY);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param group     分组
     * @param sessionId sessionId
     */
    public static void clearCache(String group, String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.hshDelete(getCacheKey(group), sessionId);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 查询缓存
     *
     * @param group 分组
     */
    public static List<String> getSessionIds(String group) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        Set<String> set;
        try {
            set = ObjectUtils.defaultIfNull(redisHelper.hshKeys(getCacheKey(group)), new HashSet<>());
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return new ArrayList<>(set);
    }
}
