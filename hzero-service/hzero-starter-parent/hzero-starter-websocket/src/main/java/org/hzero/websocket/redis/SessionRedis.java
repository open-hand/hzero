package org.hzero.websocket.redis;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.redis.RedisHelper;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.vo.SessionVO;

/**
 * 自定义session存储
 *
 * @author shuangfei.zhu@hand-china.com 2020/09/01 20:02
 */
public class SessionRedis extends WebSocketRedis {

    /**
     * 生成redis存储key
     *
     * @param sessionId webSocket的sessionId
     * @return key
     */
    private static String getCacheKey(String sessionId) {
        return WebSocketConstant.REDIS_KEY + ":sessions:" + sessionId;
    }

    /**
     * 添加缓存
     *
     * @param session session信息
     */
    public static void addCache(SessionVO session) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.strSet(getCacheKey(session.getSessionId()), redisHelper.toJson(session));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 刪除缓存
     *
     * @param sessionId webSocketSessionId
     */
    public static void clearCache(String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        try {
            redisHelper.delKey(getCacheKey(sessionId));
        } finally {
            redisHelper.clearCurrentDatabase();
        }
    }

    /**
     * 查询缓存
     *
     * @param sessionId webSocketSessionId
     */
    public static SessionVO getSession(String sessionId) {
        RedisHelper redisHelper = getRedisHelper();
        redisHelper.setCurrentDatabase(getConfig().getRedisDb());
        SessionVO session = null;
        try {
            String result = redisHelper.strGet(getCacheKey(sessionId));
            if (StringUtils.isNotBlank(result)) {
                session = redisHelper.fromJson(result, SessionVO.class);
            }
        } finally {
            redisHelper.clearCurrentDatabase();
        }
        return session;
    }
}
