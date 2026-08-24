package org.hzero.websocket.init;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.listener.SocketMessageListener;
import org.hzero.websocket.redis.*;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.hzero.websocket.registry.UserSessionRegistry;
import org.hzero.websocket.vo.SessionVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 初始化心跳注册
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/30 15:30
 */
@Component
public class ClientInit implements CommandLineRunner {

    @Autowired
    @Qualifier("ws-container")
    private RedisMessageListenerContainer container;
    @Autowired
    private SocketMessageListener listener;
    @Autowired
    private RedisConnectionFactory connectionFactory;
    @Autowired
    @Qualifier("websocket-check-executor")
    private AsyncTaskExecutor taskExecutor;

    private static final Logger logger = LoggerFactory.getLogger(ClientInit.class);

    @Override
    public void run(String... args) throws Exception {

        // 创建定时线程
        ScheduledExecutorService scheduledExecutorService =
                new ScheduledThreadPoolExecutor(5, new BasicThreadFactory.Builder()
                        .namingPattern("websocket-client-register")
                        .daemon(true)
                        .build());
        scheduledExecutorService.scheduleWithFixedDelay(new ClientInit.ClientRegister(), 0, 10, TimeUnit.SECONDS);
        // 在线用户自检线程
        scheduledExecutorService.scheduleWithFixedDelay(new ClientInit.Check(), 0, 600, TimeUnit.SECONDS);
    }

    class ClientRegister implements Runnable {

        @Override
        public void run() {
            try {
                String brokerId = BaseSessionRegistry.getBrokerId();
                // 刷新心跳缓存
                BrokerRedis.refreshCache(brokerId);
                BrokerListenRedis.refreshCache(brokerId);

                // 下面的检查逻辑，异步执行，防止执行时间超过5秒导致当前客户端下线
                taskExecutor.execute(() -> {
                    try {
                        // 检查其他客户端状态
                        List<String> brokerList = BrokerListenRedis.getCache();
                        for (String item : brokerList) {
                            if (BrokerRedis.isAlive(item)) {
                                continue;
                            }
                            BrokerListenRedis.clearRedisCache(item);
                            List<String> sessionIds = BrokerSessionRedis.getSessionIds(item);
                            // 清除节点
                            BrokerSessionRedis.clearCache(item);
                            for (String sessionId : sessionIds) {
                                SessionVO session = SessionRedis.getSession(sessionId);
                                SessionRedis.clearCache(sessionId);
                                if (session == null) {
                                    continue;
                                }
                                Long userId = session.getUserId();
                                if (userId != null) {
                                    // 清除在线用户
                                    OnlineUserRedis.deleteCache(session);
                                    // 清理用户session
                                    UserSessionRedis.clearCache(userId, sessionId);
                                }
                                String group = session.getGroup();
                                if (StringUtils.isNotBlank(group)) {
                                    // 清理group的session
                                    GroupSessionRedis.clearCache(group, sessionId);
                                }
                            }
                        }
                        // 检查channel监听是否有效
                        logger.debug("websocket container running: {}", container.isRunning());
                        if (!container.isRunning()) {
                            logger.info("websocket container Reinitialize......");
                            container = new RedisMessageListenerContainer();
                            container.setConnectionFactory(connectionFactory);
                            container.addMessageListener(new MessageListenerAdapter(listener, "messageListener"), new PatternTopic(WebSocketConstant.CHANNEL));
                            container.addMessageListener(new MessageListenerAdapter(listener, "messageListener"), new PatternTopic(BaseSessionRegistry.getBrokerId()));
                        }
                    } catch (Exception e) {
                        logger.error("exception:", e);
                    }
                });
            } catch (Exception e) {
                logger.warn("websocket register error!", e);
            }
        }
    }

    static class Check implements Runnable {

        private final RedisHelper redisHelper = ApplicationContextHelper.getContext().getBean(RedisHelper.class);

        @Override
        public void run() {
            try {
                String brokerId = UserSessionRegistry.getBrokerId();
                List<String> liveBrokerList = BrokerListenRedis.getCache();
                // 分批查询
                int page = 0;
                int size = 500;
                List<SessionVO> sessionList;
                do {
                    sessionList = OnlineUserRedis.getCache(page, size);
                    for (SessionVO item : sessionList) {
                        String sessionId = item.getSessionId();
                        // 所属客户端已下线
                        if (StringUtils.isBlank(item.getBrokerId()) || !liveBrokerList.contains(item.getBrokerId())) {
                            SessionRedis.clearCache(sessionId);
                            // 清除在线用户
                            OnlineUserRedis.deleteCache(item);
                            // 清除用户session
                            UserSessionRedis.clearCache(item.getUserId(), sessionId);
                            // 清理节点session
                            BrokerSessionRedis.clearCache(item.getBrokerId(), sessionId);
                            continue;
                        }
                        // 只处理本客户端的
                        if (!Objects.equals(item.getBrokerId(), brokerId)) {
                            continue;
                        }
                        // access_token已失效
                        if (checkAccessToken(item.getAccessToken())) {
                            clear(item);
                            continue;
                        }
                        WebSocketSession webSocketSession = UserSessionRegistry.getSession(sessionId);
                        // websocket已失效
                        if (webSocketSession == null || !webSocketSession.isOpen()) {
                            clear(item);
                        }
                    }
                    page++;
                } while (sessionList.size() >= size);
            } catch (Exception e) {
                logger.warn("online user check error!", e);
            }
        }

        private void clear(SessionVO session) {
            String sessionId = session.getSessionId();
            SessionRedis.clearCache(sessionId);
            // 清除在线用户
            OnlineUserRedis.deleteCache(session);
            // 清除用户session
            UserSessionRedis.clearCache(session.getUserId(), sessionId);
            // 清理节点session
            BrokerSessionRedis.clearCache(session.getBrokerId(), sessionId);
            // 清理内存
            UserSessionRegistry.removeSession(sessionId);
        }

        private boolean checkAccessToken(String accessToken) {
            redisHelper.setCurrentDatabase(HZeroService.Oauth.REDIS_DB);
            String key = "access_token:access:" + accessToken;
            boolean unable = true;
            if (redisHelper.hasKey(key)) {
                unable = false;
            }
            redisHelper.clearCurrentDatabase();
            return unable;
        }
    }
}
