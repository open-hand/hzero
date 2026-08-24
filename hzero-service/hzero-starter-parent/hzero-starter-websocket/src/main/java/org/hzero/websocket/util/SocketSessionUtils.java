package org.hzero.websocket.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.apache.commons.collections4.CollectionUtils;

import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.hzero.websocket.registry.GroupSessionRegistry;
import org.hzero.websocket.registry.UserSessionRegistry;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
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

    /**
     * 获取值
     *
     * @param session session 对象
     * @return accessToken 值
     */
    @Nullable
    public static String getValue(@NonNull WebSocketSession session, @NonNull String[] keys) {
        return getValue(session.getAttributes(), keys);
    }

    /**
     * 获取值
     *
     * @param attributes attributes 对象
     * @return accessToken 值
     */
    @Nullable
    public static String getValue(@NonNull Map<String, Object> attributes, @NonNull String[] keys) {
        String value = null;
        for (String key : keys) {
            value = Optional.ofNullable(attributes.get(key)).map(String::valueOf).orElse(null);
            if (StringUtils.isNotBlank(value)) {
                return value;
            }
        }

        // 返回结果
        return value;
    }

    /**
     * 获取 accessToken
     *
     * @param attributes attributes 对象
     * @return accessToken 值
     */
    @Nullable
    public static String getAccessToken(@NonNull Map<String, Object> attributes) {
        return getValue(attributes, WebSocketConstant.Attributes.TOKENS);
    }

    /**
     * 获取 accessToken
     *
     * @param session session 对象
     * @return accessToken 值
     */
    @Nullable
    public static String getAccessToken(@NonNull WebSocketSession session) {
        return getAccessToken(session.getAttributes());
    }

    /**
     * 获取必定存在的 accessToken
     *
     * @param session session 对象
     * @return accessToken 值
     */
    @NonNull
    public static String getExistsAccessToken(@NonNull WebSocketSession session) {
        return Objects.requireNonNull(getAccessToken(session));
    }

    /**
     * 判断 accessToken 是否存在
     *
     * @param session session 对象
     * @return accessToken 是否存在
     */
    public static boolean containsAccessToken(@NonNull WebSocketSession session) {
        return StringUtils.isNotBlank(getAccessToken(session));
    }

    /**
     * 判断 accessToken 是否存在
     *
     * @param attributes attributes 对象
     * @return accessToken 是否存在
     */
    public static boolean containsAccessToken(@NonNull Map<String, Object> attributes) {
        return StringUtils.isNotBlank(getAccessToken(attributes));
    }

    /**
     * 获取 SecretKey
     *
     * @param attributes attributes 对象
     * @return SecretKey 值
     */
    @Nullable
    public static String getSecretKey(@NonNull Map<String, Object> attributes) {
        return getValue(attributes, WebSocketConstant.Attributes.SECRET_KEYS);
    }

    /**
     * 获取 SecretKey
     *
     * @param session session 对象
     * @return SecretKey 值
     */
    @Nullable
    public static String getSecretKey(@NonNull WebSocketSession session) {
        return getSecretKey(session.getAttributes());
    }

    /**
     * 获取必定存在的 SecretKey
     *
     * @param session session 对象
     * @return SecretKey 值
     */
    @NonNull
    public static String getExistsSecretKey(@NonNull WebSocketSession session) {
        return Objects.requireNonNull(getSecretKey(session));
    }

    /**
     * 判断 SecretKey 是否存在
     *
     * @param session session 对象
     * @return SecretKey 是否存在
     */
    public static boolean containsSecretKey(@NonNull WebSocketSession session) {
        return StringUtils.isNotBlank(getSecretKey(session));
    }

    /**
     * 判断 SecretKey 是否存在
     *
     * @param attributes attributes 对象
     * @return SecretKey 是否存在
     */
    public static boolean containsSecretKey(@NonNull Map<String, Object> attributes) {
        return StringUtils.isNotBlank(getSecretKey(attributes));
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
