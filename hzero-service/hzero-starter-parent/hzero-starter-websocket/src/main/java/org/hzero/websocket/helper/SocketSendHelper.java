package org.hzero.websocket.helper;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.hzero.core.base.BaseConstants;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.redis.BrokerListenRedis;
import org.hzero.websocket.redis.BrokerSessionRedis;
import org.hzero.websocket.redis.SessionRedis;
import org.hzero.websocket.redis.UserSessionRedis;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.hzero.websocket.registry.UserSessionRegistry;
import org.hzero.websocket.util.SocketSessionUtils;
import org.hzero.websocket.vo.MsgVO;
import org.hzero.websocket.vo.SessionVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.core.exception.CommonException;

/**
 * token连接webSocket发送工具
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/05 15:16
 */
@Component
public class SocketSendHelper {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 指定sessionId发送webSocket消息
     *
     * @param sessionId sessionId
     * @param key       自定义的key
     * @param message   消息内容
     */
    public void sendBySession(String sessionId, String key, String message) {
        try {
            MsgVO msg = new MsgVO().setSessionId(sessionId).setKey(key).setMessage(message).setType(WebSocketConstant.SendType.SESSION).setBrokerId(BaseSessionRegistry.getBrokerId());
            String msgStr = objectMapper.writeValueAsString(msg);
            // 优先本地消费
            WebSocketSession webSocketSession = UserSessionRegistry.getSession(sessionId);
            if (webSocketSession != null) {
                SocketSessionUtils.sendMsg(webSocketSession, sessionId, msgStr);
                return;
            }
            SessionVO session = SessionRedis.getSession(sessionId);
            if (session == null) {
                return;
            }
            // 通知目标服务
            redisTemplate.convertAndSend(session.getBrokerId(), msgStr);
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 指定sessionId发送webSocket消息
     *
     * @param sessionId sessionId
     * @param key       自定义的key
     * @param data      数据
     */
    public void sendBySession(String sessionId, String key, byte[] data) {
        try {
            MsgVO msg = new MsgVO().setSessionId(sessionId).setKey(key).setData(data).setType(WebSocketConstant.SendType.SESSION).setBrokerId(BaseSessionRegistry.getBrokerId());
            // 优先本地消费
            WebSocketSession webSocketSession = UserSessionRegistry.getSession(sessionId);
            if (webSocketSession != null) {
                SocketSessionUtils.sendMsg(webSocketSession, sessionId, data);
                return;
            }
            SessionVO session = SessionRedis.getSession(sessionId);
            if (session == null) {
                return;
            }
            // 通知目标服务
            redisTemplate.convertAndSend(session.getBrokerId(), objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 指定用户发送webSocket消息
     *
     * @param userId  用户Id
     * @param key     自定义的key
     * @param message 消息内容
     */
    public void sendByUserId(Long userId, String key, String message) {
        try {
            String brokerId = BaseSessionRegistry.getBrokerId();
            MsgVO msg = new MsgVO().setUserId(userId).setKey(key).setMessage(message).setType(WebSocketConstant.SendType.USER).setBrokerId(brokerId);
            String msgStr = objectMapper.writeValueAsString(msg);
            // 优先本地消费
            List<String> sessionIdList = UserSessionRedis.getSessionIds(userId);
            SocketSessionUtils.sendUserMsg(sessionIdList, msgStr);
            // 通知其他服务
            notice(userId, msgStr);
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 指定用户发送webSocket消息
     *
     * @param userId 用户Id
     * @param key    自定义的key
     * @param data   数据
     */
    public void sendByUserId(Long userId, String key, byte[] data) {
        try {
            String brokerId = BaseSessionRegistry.getBrokerId();
            MsgVO msg = new MsgVO().setUserId(userId).setKey(key).setData(data).setType(WebSocketConstant.SendType.USER).setBrokerId(brokerId);
            // 优先本地消费
            List<String> sessionIdList = UserSessionRedis.getSessionIds(userId);
            SocketSessionUtils.sendUserMsg(sessionIdList, data);
            // 通知其他服务
            notice(userId, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 向所有用户发送webSocket消息
     *
     * @param key     自定义的key
     * @param message 消息内容
     */
    public void sendToAll(String key, String message) {
        try {
            String brokerId = BaseSessionRegistry.getBrokerId();
            MsgVO msg = new MsgVO().setKey(key).setMessage(message).setType(WebSocketConstant.SendType.ALL).setBrokerId(brokerId);
            String msgStr = objectMapper.writeValueAsString(msg);
            // 优先本地消费
            List<WebSocketSession> userSessionList = UserSessionRegistry.getAllSession();
            for (WebSocketSession item : userSessionList) {
                SocketSessionUtils.sendMsg(item, msg.getSessionId(), msgStr);
            }
            // 通知其他服务
            redisTemplate.convertAndSend(WebSocketConstant.CHANNEL, msgStr);
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 向所有用户发送webSocket消息
     *
     * @param key  自定义的key
     * @param data 数据
     */
    public void sendToAll(String key, byte[] data) {
        try {
            String brokerId = BaseSessionRegistry.getBrokerId();
            MsgVO msg = new MsgVO().setKey(key).setData(data).setType(WebSocketConstant.SendType.ALL).setBrokerId(brokerId);
            // 优先本地消费
            List<WebSocketSession> userSessionList = UserSessionRegistry.getAllSession();
            for (WebSocketSession item : userSessionList) {
                SocketSessionUtils.sendMsg(item, msg.getSessionId(), data);
            }
            // 通知其他服务
            redisTemplate.convertAndSend(WebSocketConstant.CHANNEL, objectMapper.writeValueAsString(msg));
        } catch (JsonProcessingException e) {
            throw new CommonException(BaseConstants.ErrorCode.ERROR, e);
        }
    }

    /**
     * 指定服务通道广播
     */
    private void notice(Long userId, String msgStr) {
        // 获取所有实例
        String brokerId = BaseSessionRegistry.getBrokerId();
        List<String> brokerList = BrokerListenRedis.getCache();
        List<String> userSession = UserSessionRedis.getSessionIds(userId);
        brokerList.forEach(item -> {
            if (Objects.equals(item, brokerId)) {
                // 本节点不用处理
                return;
            }
            List<String> brokerSession = BrokerSessionRedis.getSessionIds(item);
            brokerSession.retainAll(userSession);
            if (CollectionUtils.isNotEmpty(brokerSession)) {
                // 该节点有指定用户session信息，发送通知
                redisTemplate.convertAndSend(item, msgStr);
            }
        });
    }
}
