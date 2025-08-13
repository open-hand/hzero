package org.hzero.websocket.registry;

import org.apache.commons.lang3.StringUtils;
import org.hzero.core.util.UUIDUtils;
import org.hzero.websocket.redis.*;
import org.hzero.websocket.vo.SessionVO;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/04/22 10:55
 */
public abstract class BaseSessionRegistry {

    protected BaseSessionRegistry() {
    }

    private static final String BROKER_ID = UUIDUtils.generateUUID();

    public static String getBrokerId() {
        return BROKER_ID;
    }

    /**
     * 清理session内存及缓存
     *
     * @param sessionId sessionId
     */
    public static void clearSession(String sessionId) {
        SessionVO session = SessionRedis.getSession(sessionId);
        SessionRedis.clearCache(sessionId);
        if (session == null) {
            return;
        }
        // 清理服务节点session
        BrokerSessionRedis.clearCache(session.getBrokerId(), sessionId);
        Long userId = session.getUserId();
        if (userId != null) {
            // 清除在线用户
            OnlineUserRedis.deleteCache(session);
            // 清理用户session
            UserSessionRedis.clearCache(userId, sessionId);
            // 清理内存
            UserSessionRegistry.removeSession(sessionId);
        }
        String group = session.getGroup();
        if (StringUtils.isNotBlank(group)) {
            // 清理group的session
            GroupSessionRedis.clearCache(group, sessionId);
            // 清理内存
            GroupSessionRegistry.removeSession(sessionId);
        }
    }
}
