package org.hzero.websocket.handler;

import org.springframework.web.socket.*;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2020/04/28 16:40
 */
public interface SocketHandler {

    /**
     * 执行的processor
     *
     * @return processor
     */
    String processor();

    /**
     * webSocket连接创建后调用
     *
     * @param session session
     */
    void afterConnectionEstablished(WebSocketSession session);

    /**
     * 连接出错会调用
     *
     * @param session   session
     * @param exception exception
     */
    void handleTransportError(WebSocketSession session, Throwable exception);

    /**
     * 连接关闭会调用
     *
     * @param session session
     * @param status  status
     */
    void afterConnectionClosed(WebSocketSession session, CloseStatus status);

    /**
     * 处理文本消息
     *
     * @param session session
     * @param message message
     * @throws Exception exception
     */
    void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception;

    /**
     * 处理二进制消息
     *
     * @param session session
     * @param message message
     * @throws Exception exception
     */
    void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception;

    /**
     * 处理pong消息
     *
     * @param session session
     * @param message message
     * @throws Exception exception
     */
    void handlePongMessage(WebSocketSession session, PongMessage message) throws Exception;
}