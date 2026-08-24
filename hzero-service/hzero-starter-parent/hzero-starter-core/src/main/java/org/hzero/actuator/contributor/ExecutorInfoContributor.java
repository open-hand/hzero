package org.hzero.actuator.contributor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import org.springframework.boot.actuate.info.Info;
import org.springframework.boot.actuate.info.InfoContributor;

import org.hzero.core.util.CommonExecutor;

/**
 * 暴露线程池状态
 *
 * @author bojiangzhou 2020/06/30
 */
public class ExecutorInfoContributor implements InfoContributor {

    @Override
    public void contribute(Info.Builder builder) {
        Map<String, ThreadPoolExecutor> executorMap = CommonExecutor.ExecutorManager.getAllThreadPoolExecutor();

        Map<String, Map<String, Object>> executors = executorMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> threadPoolInfo(e.getValue())));

        builder.withDetail("ThreadPoolExecutorStatus", executors);
    }

    private static Map<String, Object> threadPoolInfo(ThreadPoolExecutor threadPool) {
        Map<String, Object> info = new HashMap<>(8);
        info.put("Terminated", threadPool.isTerminated());// 线程是否被终止
        info.put("PoolSize", threadPool.getPoolSize());// 线程池工作线程数
        info.put("CorePoolSize", threadPool.getCorePoolSize());// 线程池核心线程数
        info.put("MaximumPoolSize", threadPool.getMaximumPoolSize());// 线程池最大线程数
        info.put("LargestPoolSize", threadPool.getLargestPoolSize());// 最大达到过的线程数
        info.put("CompletedTaskCount", threadPool.getCompletedTaskCount());// 已完成的任务数
        info.put("TaskCount", threadPool.getTaskCount());// 总任务数
        info.put("QueueSize", threadPool.getQueue().size());// 队列大小
        info.put("QueueRemainingCapacity", threadPool.getQueue().remainingCapacity());// 队列剩余容量
        return info;
    }
}
