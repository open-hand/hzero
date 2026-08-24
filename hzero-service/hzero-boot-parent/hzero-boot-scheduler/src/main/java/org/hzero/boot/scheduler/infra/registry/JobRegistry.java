package org.hzero.boot.scheduler.infra.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * job存储类
 *
 * @author shuangfei.zhu@hand-china.com 2019/01/16 16:12
 */
public class JobRegistry {

    private JobRegistry() {
    }

    private static Map<String, Object> jobMap = new ConcurrentHashMap<>();

    public static void addJobHandler(String jobCode, Object handler) {
        jobMap.put(jobCode, handler);
    }

    public static Object getJobHandler(String jobHandler) {
        return jobMap.get(jobHandler);
    }
}
