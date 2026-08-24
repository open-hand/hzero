package org.hzero.websocket.registry;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.WebSocketSession;

/**
 * WebSocketSession存储
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/18 17:19
 */
public class UserSessionRegistry extends BaseSessionRegistry {

    private UserSessionRegistry() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSessionRegistry.class);

    /**
     * 内存存储webSocketSession  sessionId webSocketSession
     */
    private static final Map<String, WebSocketSession> SESSION_MAP = new ConcurrentHashMap<>();

    /**
     * 添加session存储
     */
    public static void addSession(WebSocketSession session, String sessionId) {
        SESSION_MAP.put(sessionId, session);
    }

    /**
     * 移除session存储
     */
    public static void removeSession(String sessionId) {
        try {
            // 关闭长连接
            WebSocketSession session = SESSION_MAP.get(sessionId);
            if (session != null) {
                session.close();
            }
        } catch (IOException e) {
            LOGGER.trace("close websocket failed! sessionId : {}", sessionId);
        } finally {
            // 移除sessionId webSocketSession
            SESSION_MAP.remove(sessionId);
        }
    }

    /**
     * 获取WebSocketSession
     */
    public static WebSocketSession getSession(String sessionId) {
        if (MapUtils.isEmpty(SESSION_MAP)) {
            return null;
        }
        return SESSION_MAP.get(sessionId);
    }

    /**
     * 获取WebSocketSession
     */
    public static List<WebSocketSession> getAllSession() {
        if (MapUtils.isEmpty(SESSION_MAP)) {
            return Collections.emptyList();
        }
        return new ArrayList<>(SESSION_MAP.values());
    }
}
