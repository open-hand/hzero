package org.hzero.websocket.redis;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hzero.core.redis.RedisHelper;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.vo.SessionVO;

/**
 * 在线用户缓存工具
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/11 20:19
 */
public class OnlineUserRedis extends WebSocketRedis {

    /**
     * 生成redis存储key
     *
     * @return key
     */
    private static String getCacheKey() {
        return WebSocketConstant.REDIS_KEY + ":online-users:all";
    }

    /**
     * 生成redis存储key
     *
     * @return key
     */
    private static String getCacheKey(Long tenantId) {
        return WebSocketConstant.REDIS_KEY + ":online-users:tenant:" + tenantId;
    }

    /**
     * 刷新缓存
     *
     * @param session session信息
     */
    public static void refreshCache(SessionVO session) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            // 缓存记录sessionId 与用户的关系，还要考虑分页排序
            long date = System.nanoTime();
            redisHelper.zSetAdd(getCacheKey(), session.getSessionId(), date);
            redisHelper.zSetAdd(getCacheKey(session.getTenantId()), session.getSessionId(), date);
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param session session信息
     */
    public static void deleteCache(SessionVO session) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.zSetRemove(getCacheKey(), session.getSessionId());
            redisHelper.zSetRemove(getCacheKey(session.getTenantId()), session.getSessionId());
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param sessionList session信息
     */
    public static void deleteCache(List<SessionVO> sessionList) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            for (SessionVO session : sessionList) {
                redisHelper.zSetRemove(getCacheKey(), session.getSessionId());
                redisHelper.zSetRemove(getCacheKey(session.getTenantId()), session.getSessionId());
            }
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 获取在线人数
     */
    public static Long getSize() {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        Long total;
        try {
            total = redisHelper.zSetSize(getCacheKey());
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return total;
    }

    /**
     * 指定租户获取在线人数
     */
    public static Long getSize(Long tenantId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        Long total;
        try {
            total = redisHelper.zSetSize(getCacheKey(tenantId));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return total;
    }

    /**
     * 分页查询在线用户
     *
     * @param page 页
     * @param size 每页数量
     */
    public static List<SessionVO> getCache(int page, int size) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        List<SessionVO> list = new ArrayList<>();
        try {
            int start = size * page;
            int end = start + size - 1;
            Set<String> keySets = redisHelper.zSetRange(getCacheKey(), (long) start, (long) end);
            keySets.forEach(item -> {
                SessionVO session = SessionRedis.getSession(item);
                if (session != null && session.getUserId() != null) {
                    list.add(session);
                }
            });
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return list;
    }

    /**
     * 分页查询在线用户
     *
     * @param page 页
     * @param size 每页数量
     */
    public static List<SessionVO> getCache(int page, int size, long tenantId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        List<SessionVO> list = new ArrayList<>();
        try {
            int start = size * page;
            int end = start + size - 1;
            Set<String> keySets = redisHelper.zSetRange(getCacheKey(tenantId), (long) start, (long) end);
            keySets.forEach(item -> {
                SessionVO session = SessionRedis.getSession(item);
                if (session != null && session.getUserId() != null) {
                    list.add(session);
                }
            });
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return list;
    }

    /**
     * 查询所有在线用户
     */
    public static List<SessionVO> getCache() {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        List<SessionVO> list = new ArrayList<>();
        try {
            Long count = redisHelper.zSetSize(getCacheKey());
            Set<String> keySets = redisHelper.zSetRange(getCacheKey(), 0L, count);
            keySets.forEach(item -> {
                SessionVO session = SessionRedis.getSession(item);
                if (session != null && session.getUserId() != null) {
                    list.add(session);
                }
            });
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return list;
    }

    /**
     * 指定租户查询所有在线用户
     */
    public static List<SessionVO> getCache(Long tenantId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        List<SessionVO> list = new ArrayList<>();
        try {
            Long count = redisHelper.zSetSize(getCacheKey());
            Set<String> keySets = redisHelper.zSetRange(getCacheKey(tenantId), 0L, count);
            keySets.forEach(item -> {
                SessionVO session = SessionRedis.getSession(item);
                if (session != null && session.getUserId() != null) {
                    list.add(session);
                }
            });
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return list;
    }
}
