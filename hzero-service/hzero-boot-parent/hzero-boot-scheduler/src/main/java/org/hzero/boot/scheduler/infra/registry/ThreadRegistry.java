package org.hzero.boot.scheduler.infra.registry;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/02/15 16:36
 */
public class ThreadRegistry {

    private ThreadRegistry() {
    }

    /**
     * <Thread, jobId>
     */
    private static Map<Thread, Long> jobThreadMap = new ConcurrentHashMap<>();

    /**
     * <threadId, iJobHandler>
     */
    private static Map<Long, Object> jobHandlerMap = new ConcurrentHashMap<>();


    public static List<Thread> getThread(Long jobId) {
        List<Thread> list = new ArrayList<>();
        jobThreadMap.forEach((k, v) -> {
            if (Objects.equals(v, jobId)) {
                list.add(k);
            }
        });
        return list;
    }

    public static void addThread(Thread thread, Long jobId) {
        jobThreadMap.put(thread, jobId);
    }

    public static void deleteThread(Thread thread) {
        jobThreadMap.remove(thread);
    }

    public static Object getJobHandler(Long threadId) {
        return jobHandlerMap.get(threadId);
    }

    public static void addJobHandler(Long threadId, Object jobHandler) {
        jobHandlerMap.put(threadId, jobHandler);
    }

    public static void deleteJobHandler(Long threadId) {
        jobHandlerMap.remove(threadId);
    }
}
