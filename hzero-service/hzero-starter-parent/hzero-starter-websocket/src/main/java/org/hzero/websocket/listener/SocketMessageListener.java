package org.hzero.websocket.listener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.redis.GroupSessionRedis;
import org.hzero.websocket.redis.UserSessionRedis;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.hzero.websocket.registry.GroupSessionRegistry;
import org.hzero.websocket.registry.UserSessionRegistry;
import org.hzero.websocket.util.SocketSessionUtils;
import org.hzero.websocket.vo.MsgVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

/**
 * redis 通道消息监听
 *
 * @author shuangfei.zhu@hand-china.com 2019/04/19 15:52
 */
@Component
public class SocketMessageListener {

    private static final Logger logger = LoggerFactory.getLogger(SocketMessageListener.class);

    private final ObjectMapper objectMapper;

    @Autowired
    public SocketMessageListener(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public void messageListener(String msgVO) {
        try {
            // 监听到消息发送webSocket消息
            String brokerId = BaseSessionRegistry.getBrokerId();
            MsgVO msg = objectMapper.readValue(msgVO, MsgVO.class);
            if (Objects.equals(brokerId, msg.getBrokerId())) {
                // 消息发送方为本服务，不处理
                return;
            }
            List<String> sessionIdList;
            byte[] data = msg.getData();
            Long userId = msg.getUserId();
            String group = msg.getGroup();
            switch (msg.getType()) {
                case WebSocketConstant.SendType.SESSION:
                    WebSocketSession session = UserSessionRegistry.getSession(msg.getSessionId());
                    if (data != null) {
                        SocketSessionUtils.sendMsg(session, msg.getSessionId(), data);
                    } else {
                        SocketSessionUtils.sendMsg(session, msg.getSessionId(), objectMapper.writeValueAsString(msg));
                    }
                    break;
                case WebSocketConstant.SendType.USER:
                    sessionIdList = UserSessionRedis.getSessionIds(userId);
                    if (data != null) {
                        SocketSessionUtils.sendUserMsg(sessionIdList, data);
                    } else {
                        SocketSessionUtils.sendUserMsg(sessionIdList, objectMapper.writeValueAsString(msg));
                    }
                    break;
                case WebSocketConstant.SendType.ALL:
                    List<WebSocketSession> userSessionList = UserSessionRegistry.getAllSession();
                    for (WebSocketSession item : userSessionList) {
                        if (data != null) {
                            SocketSessionUtils.sendMsg(item, msg.getSessionId(), data);
                        } else {
                            SocketSessionUtils.sendMsg(item, msg.getSessionId(), objectMapper.writeValueAsString(msg));
                        }
                    }
                    break;
                case WebSocketConstant.SendType.S_SESSION:
                    WebSocketSession clientSession = GroupSessionRegistry.getSession(msg.getSessionId());
                    if (data != null) {
                        SocketSessionUtils.sendMsg(clientSession, msg.getSessionId(), data);
                    } else {
                        SocketSessionUtils.sendMsg(clientSession, msg.getSessionId(), objectMapper.writeValueAsString(msg));
                    }
                    break;
                case WebSocketConstant.SendType.S_GROUP:
                    sessionIdList = GroupSessionRedis.getSessionIds(group);
                    if (data != null) {
                        SocketSessionUtils.sendGroupMsg(sessionIdList, data);
                    } else {
                        SocketSessionUtils.sendGroupMsg(sessionIdList, objectMapper.writeValueAsString(msg));
                    }
                    break;
                case WebSocketConstant.SendType.S_ALL:
                    List<WebSocketSession> groupSessionList = GroupSessionRegistry.getAllSession();
                    for (WebSocketSession item : groupSessionList) {
                        if (data != null) {
                            SocketSessionUtils.sendMsg(item, msg.getSessionId(), data);
                        } else {
                            SocketSessionUtils.sendMsg(item, msg.getSessionId(), objectMapper.writeValueAsString(msg));
                        }
                    }
                    break;
                case WebSocketConstant.SendType.CLOSE:
                    if (userId != null) {
                        // 关闭用户连接
                        sessionIdList = UserSessionRedis.getSessionIds(userId);
                        SocketSessionUtils.closeSession(sessionIdList);
                    }
                    if (StringUtils.isNotBlank(group)) {
                        // 关闭分组连接
                        sessionIdList = GroupSessionRedis.getSessionIds(group);
                        SocketSessionUtils.closeSession(sessionIdList);
                    }
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
    }
}
