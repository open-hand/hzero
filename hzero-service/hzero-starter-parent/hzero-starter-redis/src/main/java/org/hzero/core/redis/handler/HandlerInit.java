package org.hzero.core.redis.handler;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.hzero.core.redis.HZeroRedisProperties;
import org.hzero.core.redis.RedisQueueHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.ProxyUtils;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 初始化
 *
 * @author shuangfei.zhu@hand-china.com 2019/10/23 16:15
 */
public class HandlerInit implements CommandLineRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandlerInit.class);

    private final HZeroRedisProperties redisProperties;
    private final RedisQueueHelper redisQueueHelper;

    public HandlerInit(RedisQueueHelper redisQueueHelper, HZeroRedisProperties redisProperties) {
        this.redisQueueHelper = redisQueueHelper;
        this.redisProperties = redisProperties;
    }

    @Override
    public void run(String... args) throws Exception {
        if (redisProperties != null && !redisProperties.isRedisQueue()) {
            // 若关闭redis队列则不扫描
            return;
        }
        boolean flag = scanQueueHandler();
        // 若扫描到队列消费类，则开启消费线程
        if (flag) {
            // 启动线程执行消费
            ScheduledExecutorService register =
                    new ScheduledThreadPoolExecutor(1, new BasicThreadFactory.Builder()
                            .namingPattern("redis-queue-consumer")
                            .daemon(true)
                            .build());
            register.scheduleAtFixedRate(new Listener(redisQueueHelper), 0, redisProperties == null ? 5 : redisProperties.getIntervals(), TimeUnit.SECONDS);
        }
    }

    /**
     * 启动后扫描QueueHandler注解
     */
    private boolean scanQueueHandler() {
        boolean flag = false;
        Map<String, Object> map = ApplicationContextHelper.getContext().getBeansWithAnnotation(QueueHandler.class);
        for (Object service : map.values()) {
            if (service instanceof IQueueHandler || service instanceof IBatchQueueHandler) {
                QueueHandler queueHandler = ProxyUtils.getUserClass(service).getAnnotation(QueueHandler.class);
                if (ObjectUtils.isEmpty(queueHandler)) {
                    LOGGER.debug("could not get target bean , queueHandler : {}", service);
                } else {
                    HandlerRegistry.addHandler(queueHandler.value(), service);
                    LOGGER.info("Start listening to the redis queue : {}", queueHandler.value());
                    flag = true;
                }
            }
        }
        return flag;
    }

    static class Listener implements Runnable {

        private final RedisQueueHelper redisQueueHelper;

        Listener(RedisQueueHelper redisQueueHelper) {
            this.redisQueueHelper = redisQueueHelper;
        }

        @Override
        public void run() {
            Set<String> keys = HandlerRegistry.getKeySet();
            if (CollectionUtils.isEmpty(keys)) {
                return;
            }
            keys.forEach(key -> HandlerRegistry.getThreadPool(key).execute(new Consumer(key, redisQueueHelper)));
        }
    }

    /**
     * 消费线程
     */
    static class Consumer implements Runnable {

        private final String key;
        private final RedisQueueHelper redisQueueHelper;

        public Consumer(String key, RedisQueueHelper redisQueueHelper) {
            this.key = key;
            this.redisQueueHelper = redisQueueHelper;
        }

        @Override
        public void run() {
            Object handler = HandlerRegistry.getHandler(key);
            if (handler == null) {
                return;
            }
            if (handler instanceof IQueueHandler) {
                while (true) {
                    String message = redisQueueHelper.pull(key);
                    if (StringUtils.isBlank(message)) {
                        return;
                    }
                    ((IQueueHandler) handler).process(message);
                }
            } else if (handler instanceof IBatchQueueHandler) {
                IBatchQueueHandler batchQueueHandler = (IBatchQueueHandler) handler;
                int size = batchQueueHandler.getSize();
                if (size <= 0) {
                    batchQueueHandler.process(redisQueueHelper.pullAll(key));
                } else {
                    while (true) {
                        List<String> list = redisQueueHelper.pullAll(key, size);
                        if (CollectionUtils.isEmpty(list)) {
                            return;
                        }
                        batchQueueHandler.process(list);
                    }
                }
            }
        }
    }
}
