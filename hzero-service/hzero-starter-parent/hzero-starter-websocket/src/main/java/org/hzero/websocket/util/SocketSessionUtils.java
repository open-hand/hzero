package org.hzero.websocket.util;

import java.io.IOException;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.hzero.websocket.registry.GroupSessionRegistry;
import org.hzero.websocket.registry.UserSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/05/15 9:47
 */
public class SocketSessionUtils {

    private SocketSessionUtils() {
    }

    private static final Logger logger = LoggerFactory.getLogger(SocketSessionUtils.class);

    public static void sendUserMsg(List<String> sessionIds, String msgVO) {
        for (String sessionId : sessionIds) {
            WebSocketSession session = UserSessionRegistry.getSession(sessionId);
            if (session == null) {
                // websocketSession不在当前节点
                continue;
            }
            sendMsg(session, sessionId, msgVO);
        }
    }

    public static void sendUserMsg(List<String> sessionIds, byte[] data) {
        for (String sessionId : sessionIds) {
            WebSocketSession session = UserSessionRegistry.getSession(sessionId);
            if (session == null) {
                // websocketSession不在当前节点
                continue;
            }
            sendMsg(session, sessionId, data);
        }
    }

    public static void sendGroupMsg(List<String> sessionIds, String msgVO) {
        for (String sessionId : sessionIds) {
            WebSocketSession session = GroupSessionRegistry.getSession(sessionId);
            if (session == null) {
                // websocketSession不在当前节点
                continue;
            }
            sendMsg(session, sessionId, msgVO);
        }
    }

    public static void sendGroupMsg(List<String> sessionIds, byte[] data) {
        for (String sessionId : sessionIds) {
            WebSocketSession session = GroupSessionRegistry.getSession(sessionId);
            if (session == null) {
                // websocketSession不在当前节点
                continue;
            }
            sendMsg(session, sessionId, data);
        }
    }

    public static void sendMsg(WebSocketSession session, String sessionId, String msgVO) {
        if (session == null) {
            // websocketSession不在当前节点
            return;
        }
        if (!session.isOpen()) {
            // 清除失效连接
            BaseSessionRegistry.clearSession(sessionId);
            return;
        }
        try {
            session.sendMessage(new TextMessage(msgVO));
        } catch (IOException e) {
            logger.debug("send webSocket byte message failed! url : {}", session.getUri());
        }
    }

    public static void sendMsg(WebSocketSession session, String sessionId, byte[] data) {
        if (session == null) {
            // websocketSession不在当前节点
            return;
        }
        if (!session.isOpen()) {
            // 清除失效连接
            BaseSessionRegistry.clearSession(sessionId);
            return;
        }
        try {
            session.sendMessage(new BinaryMessage(data));
        } catch (IOException e) {
            logger.debug("send webSocket byte message failed ");
        }
    }

    public static void closeSession(List<String> sessionIds) {
        if (CollectionUtils.isEmpty(sessionIds)) {
            return;
        }
        // 清理内存及缓存
        sessionIds.forEach(BaseSessionRegistry::clearSession);
    }
}
