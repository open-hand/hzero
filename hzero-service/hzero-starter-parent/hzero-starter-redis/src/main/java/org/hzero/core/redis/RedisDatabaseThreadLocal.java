package org.hzero.core.redis;

import java.util.ArrayDeque;
import java.util.Deque;

import org.apache.commons.collections4.CollectionUtils;

/**
 * redis database ThreadLocal
 *
 * @author bojiangzhou 2018/08/28
 */
public class RedisDatabaseThreadLocal {

    private RedisDatabaseThreadLocal() {
    }

    private static final ThreadLocal<Deque<Integer>> THREAD_DB = new ThreadLocal<>();

    /**
     * 更改当前线程 RedisTemplate db
     *
     * @param db set current redis db
     */
    public static void set(int db) {
        Deque<Integer> deque = THREAD_DB.get();
        if (deque == null) {
            deque = new ArrayDeque<>();
        }
        deque.addFirst(db);
        THREAD_DB.set(deque);
        // 记录
    }

    /**
     * @return get current redis db
     */
    public static Integer get() {
        Deque<Integer> deque = THREAD_DB.get();
        if (CollectionUtils.isEmpty(deque)) {
            return null;
        }
        return deque.getFirst();
    }

    /**
     * 清理
     */
    public static void clear() {
        Deque<Integer> deque = THREAD_DB.get();
        if (deque == null || deque.size() <= 1) {
            THREAD_DB.remove();
            return;
        }
        deque.removeFirst();
        THREAD_DB.set(deque);
    }
}
