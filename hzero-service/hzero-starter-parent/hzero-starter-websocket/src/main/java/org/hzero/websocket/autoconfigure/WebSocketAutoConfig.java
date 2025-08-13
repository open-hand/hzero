package org.hzero.websocket.autoconfigure;

import org.hzero.websocket.config.WebSocketConfig;
import org.hzero.websocket.constant.WebSocketConstant;
import org.hzero.websocket.handler.WebSocketHandler;
import org.hzero.websocket.interceptor.WebSocketInterceptor;
import org.hzero.websocket.listener.SocketMessageListener;
import org.hzero.websocket.registry.BaseSessionRegistry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.listener.adapter.MessageListenerAdapter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * 包扫描
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/13 13:31
 */
@Configuration
@EnableWebSocket
@ComponentScan(basePackages = "org.hzero.websocket")
public class WebSocketAutoConfig implements WebSocketConfigurer {

    private final WebSocketConfig config;

    public WebSocketAutoConfig(WebSocketConfig config) {
        this.config = config;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // webSocket通道
        registry.addHandler(new WebSocketHandler(), config.getWebsocket())
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*");
        // sockJs通道
        registry.addHandler(new WebSocketHandler(), config.getSockJs())
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOrigins("*")
                .withSockJS();
    }

    /**
     * 消息监听器适配器，绑定消息处理器
     */
    @Bean("ws-adapter")
    MessageListenerAdapter listenerAdapter(SocketMessageListener listener) {
        return new MessageListenerAdapter(listener, "messageListener");
    }

    /**
     * redis消息监听器容器
     */
    @Bean("ws-container")
    RedisMessageListenerContainer container(RedisConnectionFactory connectionFactory, @Qualifier("ws-adapter") MessageListenerAdapter listenerAdapter) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        // 订阅通道
        container.addMessageListener(listenerAdapter, new PatternTopic(WebSocketConstant.CHANNEL));
        container.addMessageListener(listenerAdapter, new PatternTopic(BaseSessionRegistry.getBrokerId()));
        return container;
    }

    @Bean("websocket-check-executor")
    public AsyncTaskExecutor asyncTaskExecutor(WebSocketConfig config) {
        WebSocketConfig.ThreadPoolProperties threadPoolProperties = config.getThreadPoolProperties();
        if (threadPoolProperties == null) {
            threadPoolProperties = new WebSocketConfig.ThreadPoolProperties();
        }
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolProperties.getCorePoolSize());
        executor.setMaxPoolSize(threadPoolProperties.getMaxPoolSize());
        executor.setKeepAliveSeconds(threadPoolProperties.getKeepAliveSeconds());
        executor.setQueueCapacity(threadPoolProperties.getQueueCapacity());
        executor.setAllowCoreThreadTimeOut(threadPoolProperties.isAllowCoreThreadTimeOut());
        executor.setThreadNamePrefix(threadPoolProperties.getThreadNamePrefix());
        return executor;
    }
}