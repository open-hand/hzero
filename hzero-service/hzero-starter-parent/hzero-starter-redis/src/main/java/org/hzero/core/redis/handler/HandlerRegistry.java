package org.hzero.core.redis.handler;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * handler存储类
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/16 16:12
 */
public class HandlerRegistry {

    private HandlerRegistry() {
    }

    /**
     * 存储消息队列key与消费执行类
     */
    private static final Map<String, Object> HANDLER_MAP = new ConcurrentHashMap<>();
    /**
     * 存储消息队列key与消费处理线程池
     */
    private static final Map<String, ThreadPoolExecutor> THREAD_MAP = new ConcurrentHashMap<>();

    public static void addHandler(String key, Object handler) {
        if (HANDLER_MAP.containsKey(key)) {
            // 相同的key有两个实现，保留批量的
            if (HANDLER_MAP.get(key) instanceof IQueueHandler) {
                HANDLER_MAP.put(key, handler);
            }
        } else {
            HANDLER_MAP.put(key, handler);
        }
    }

    public static Object getHandler(String key) {
        return HANDLER_MAP.get(key);
    }

    public static Set<String> getKeySet() {
        return HANDLER_MAP.keySet();
    }

    public static ThreadPoolExecutor getThreadPool(String key) {
        if (!THREAD_MAP.containsKey(key)) {
            // 新建线程池，只允许一个线程存在，其余的抛弃
            ThreadPoolExecutor executor = new ThreadPoolExecutor(1, 1, 0, TimeUnit.SECONDS,
                    new ArrayBlockingQueue<>(1), new ThreadPoolExecutor.DiscardPolicy());
            THREAD_MAP.put(key, executor);
        }
        return THREAD_MAP.get(key);
    }
}
