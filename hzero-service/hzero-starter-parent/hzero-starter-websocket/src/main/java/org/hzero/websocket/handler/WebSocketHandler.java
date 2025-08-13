package org.hzero.websocket.handler;


import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.socket.*;
import org.springframework.web.socket.adapter.standard.StandardWebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;
import org.springframework.web.socket.sockjs.transport.session.WebSocketServerSockJsSession;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * webSocket处理器
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/19 9:09
 */
public class WebSocketHandler extends AbstractWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebSocketHandler.class);
    private final Map<String, SocketHandler> handlers = new HashMap<>();

    /**
     * 获取容器中所有处理器
     *
     * @return 处理器
     */
    private Map<String, SocketHandler> getHandlers() {
        if (handlers.size() > 0) {
            return handlers;
        }
        Map<String, SocketHandler> map = ApplicationContextHelper.getContext().getBeansOfType(SocketHandler.class);
        for (SocketHandler handler : map.values()) {
            handlers.put(handler.processor(), handler);
        }
        return handlers;
    }

    /**
     * webSocket连接创建后调用
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        String processor = getProcessor(session.getAttributes());
        Map<String, SocketHandler> handlerMap = getHandlers();
        if (StringUtils.isNotBlank(processor) && handlerMap.containsKey(processor)) {
            // 执行group的处理器
            handlerMap.get(processor).afterConnectionEstablished(session);
        }
        // 连接仍然正常，执行默认处理器
        if (session.isOpen()) {
            handlerMap.get(WebSocketConstant.DEFAULT_PROCESSOR).afterConnectionEstablished(session);
        }
    }

    /**
     * 连接出错会调用
     */
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) {
        // 先获取sessionId
        String sessionId = getSessionId(session);
        try {
            // 执行group自定义处理器
            String processor = getProcessor(session.getAttributes());
            Map<String, SocketHandler> handlerMap = getHandlers();
            if (StringUtils.isNotBlank(processor) && handlerMap.containsKey(processor)) {
                // 执行group的处理器
                handlerMap.get(processor).handleTransportError(session, exception);
            }
        } finally {
            BaseSessionRegistry.clearSession(sessionId);
        }
    }

    /**
     * 连接关闭会调用
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        // 先获取sessionId
        String sessionId = getSessionId(session);
        try {
            // 执行group自定义处理器
            String processor = getProcessor(session.getAttributes());
            Map<String, SocketHandler> handlerMap = getHandlers();
            if (StringUtils.isNotBlank(processor) && handlerMap.containsKey(processor)) {
                // 执行group的处理器
                handlerMap.get(processor).afterConnectionClosed(session, status);
            }
        } finally {
            BaseSessionRegistry.clearSession(sessionId);
        }
    }

    /**
     * 处理接收到的文本消息
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String processor = getProcessor(session.getAttributes());
        Map<String, SocketHandler> handlerMap = getHandlers();
        if (StringUtils.isNotBlank(processor) && handlerMap.containsKey(processor)) {
            // 执行group的处理器
            handlerMap.get(processor).handleTextMessage(session, message);
            return;
        }
        handlerMap.get(WebSocketConstant.DEFAULT_PROCESSOR).handleTextMessage(session, message);
    }

    /**
     * 处理接收到的二进制消息
     */
    @Override
    public void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        String processor = getProcessor(session.getAttributes());
        Map<String, SocketHandler> handlerMap = getHandlers();
        if (StringUtils.isNotBlank(processor) && handlerMap.containsKey(processor)) {
            // 执行group的处理器
            handlerMap.get(processor).handleBinaryMessage(session, message);
            return;
        }
        handlerMap.get(WebSocketConstant.DEFAULT_PROCESSOR).handleBinaryMessage(session, message);
    }

    /**
     * 处理接收到的pong消息
     */
    @Override
    public void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception {
        String processor = getProcessor(session.getAttributes());
        Map<String, SocketHandler> handlerMap = getHandlers();
        if (StringUtils.isNotBlank(processor) && handlerMap.containsKey(processor)) {
            // 执行group的处理器
            handlerMap.get(processor).handlePongMessage(session, message);
            return;
        }
        handlerMap.get(WebSocketConstant.DEFAULT_PROCESSOR).handlePongMessage(session, message);
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取sessionId
     *
     * @param session session
     * @return sessionId
     */
    private String getSessionId(WebSocketSession session) {
        String sessionId = null;
        try {
            // 清理缓存
            if (session instanceof StandardWebSocketSession) {
                // websocket连接方式
                sessionId = session.getId();
            } else if (session instanceof WebSocketServerSockJsSession) {
                // sock js 连接
                sessionId = ((WebSocketSession) FieldUtils.readField(session, "webSocketSession", true)).getId();
            }
            return sessionId;
        } catch (Exception e) {
            logger.warn("webSocket disConnection failed.");
            return null;
        }
    }

    private String getProcessor(Map<String, Object> attributeMap) {
        String processor = null;
        if (attributeMap.containsKey(WebSocketConstant.Attributes.PROCESSOR)) {
            processor = String.valueOf(attributeMap.get(WebSocketConstant.Attributes.PROCESSOR));
        }
        return processor;
    }
}
