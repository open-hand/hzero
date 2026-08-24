package org.hzero.core.util;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;

import io.choerodon.core.exception.CommonException;

import org.hzero.core.base.BaseConstants;

/**
 * @author bojiangzhou 2020/02/24
 */
public class CommonExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommonExecutor.class);

    private static final ThreadPoolExecutor BASE_EXECUTOR;

    static {
        String executorName = "BaseExecutor";
        BASE_EXECUTOR = buildThreadFirstExecutor(executorName);
    }


    /**
     * 获取默认构造的通用线程池，线程池核心是为 CPU 核数，最大线程数为 8倍 CPU 核数
     *
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor getCommonExecutor() {
        return BASE_EXECUTOR;
    }

    /**
     * 构建线程优先的线程池
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * <p>
     * 此方法默认设置核心线程数为 CPU 核数，最大线程数为 8倍 CPU 核数，空闲线程超过 5 分钟销毁，工作队列大小为 65536。
     *
     * @param poolName 线程池名称
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadFirstExecutor(String poolName) {
        int coreSize = CommonExecutor.getCpuProcessors();
        int maxSize = coreSize * 8;
        return buildThreadFirstExecutor(coreSize, maxSize, 5, TimeUnit.MINUTES, 1 << 16, poolName);
    }

    /**
     * 构建线程优先的线程池
     * <p>
     * 线程池默认是当核心线程数满了后，将任务添加到工作队列中，当工作队列满了之后，再创建线程直到达到最大线程数。
     *
     * <p>
     * 线程优先的线程池，就是在核心线程满了之后，继续创建线程，直到达到最大线程数之后，再把任务添加到工作队列中。
     *
     * @param corePoolSize    核心线程数
     * @param maximumPoolSize 最大线程数
     * @param keepAliveTime   空闲线程的空闲时间
     * @param unit            时间单位
     * @param workQueueSize   工作队列容量大小
     * @param poolName        线程池名称
     * @return ThreadPoolExecutor
     */
    public static ThreadPoolExecutor buildThreadFirstExecutor(int corePoolSize,
                                                              int maximumPoolSize,
                                                              long keepAliveTime,
                                                              TimeUnit unit,
                                                              int workQueueSize,
                                                              String poolName) {
        // 自定义队列，优先开启更多线程，而不是放入队列
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>(workQueueSize) {
            private static final long serialVersionUID = 5075561696269543041L;

            @Override
            public boolean offer(@Nonnull Runnable o) {
                return false; // 造成队列已满的假象
            }
        };

        // 当线程达到 maximumPoolSize 时会触发拒绝策略，此时将任务 put 到队列中
        RejectedExecutionHandler rejectedExecutionHandler = (runnable, executor) -> {
            try {
                // 任务拒绝时，通过 offer 放入队列
                queue.put(runnable);
            } catch (InterruptedException e) {
                LOGGER.warn("{} Queue offer interrupted. ", poolName, e);
                Thread.currentThread().interrupt();
            }
        };

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                corePoolSize, maximumPoolSize,
                keepAliveTime, unit,
                queue,
                new ThreadFactoryBuilder()
                        .setNameFormat(poolName + "-%d")
                        .setUncaughtExceptionHandler((Thread thread, Throwable throwable) -> {
                            LOGGER.error("{} catching the uncaught exception, ThreadName: [{}]", poolName, thread.toString(), throwable);
                        })
                        .build(),
                rejectedExecutionHandler
        );

        executor.allowCoreThreadTimeOut(true);

        ExecutorManager.registerAndMonitorThreadPoolExecutor(poolName, executor);

        return executor;
    }

    /**
     * 批量提交异步任务，使用默认的线程池
     *
     * @param tasks 将任务转化为 AsyncTask 批量提交
     */
    public static <T> List<T> batchExecuteAsync(List<AsyncTask<T>> tasks, @Nonnull String taskName) {
        return batchExecuteAsync(tasks, BASE_EXECUTOR, taskName);
    }

    /**
     * 批量提交异步任务，执行失败可抛出异常或返回异常编码即可 <br>
     * <p>
     * 需注意提交的异步任务无法控制事务，一般需容忍产生一些垃圾数据的情况下才能使用异步任务，异步任务执行失败将抛出异常，主线程可回滚事务.
     * <p>
     * 异步任务失败后，将取消剩余的任务执行.
     *
     * @param tasks    将任务转化为 AsyncTask 批量提交
     * @param executor 线程池，需自行根据业务场景创建相应的线程池
     * @return 返回执行结果
     */
    public static <T> List<T> batchExecuteAsync(@Nonnull List<AsyncTask<T>> tasks, @Nonnull ThreadPoolExecutor executor, @Nonnull String taskName) {
        if (CollectionUtils.isEmpty(tasks)) {
            return Collections.emptyList();
        }

        int size = tasks.size();

        List<Callable<T>> callables = tasks.stream().map(t -> (Callable<T>) () -> {
            try {
                T r = t.doExecute();

                LOGGER.debug("[>>Executor<<] Async task execute success. ThreadName: [{}], BatchTaskName: [{}], SubTaskName: [{}]",
                        Thread.currentThread().getName(), taskName, t.taskName());
                return r;
            } catch (Throwable e) {
                LOGGER.warn("[>>Executor<<] Async task execute error. ThreadName: [{}], BatchTaskName: [{}], SubTaskName: [{}], exception: {}",
                        Thread.currentThread().getName(), taskName, t.taskName(), e.getMessage());
                throw e;
            }
        }).collect(Collectors.toList());

        CompletionService<T> cs = new ExecutorCompletionService<>(executor, new LinkedBlockingQueue<>(size));
        List<Future<T>> futures = new ArrayList<>(size);
        LOGGER.info("[>>Executor<<] Start async tasks, BatchTaskName: [{}], TaskSize: [{}]", taskName, size);

        for (Callable<T> task : callables) {
            futures.add(cs.submit(task));
        }

        List<T> resultList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            try {
                Future<T> future = cs.poll(6, TimeUnit.MINUTES);
                if (future != null) {
                    T result = future.get();
                    resultList.add(result);

                    Object log;
                    if (result instanceof Collection) {
                        log = ((Collection<?>) result).size();
                    } else {
                        log = result;
                    }
                    LOGGER.debug("[>>Executor<<] Async task [{}] - [{}] execute success, result: {}", taskName, i, log == null ? "null" : log);
                } else {
                    cancelTask(futures);
                    LOGGER.error("[>>Executor<<] Async task [{}] - [{}] execute timeout, then cancel other tasks.", taskName, i);
                    throw new CommonException(BaseConstants.ErrorCode.TIMEOUT);
                }
            } catch (ExecutionException e) {
                LOGGER.warn("[>>Executor<<] Async task [{}] - [{}] execute error, then cancel other tasks.", taskName, i, e);
                cancelTask(futures);
                Throwable throwable = e.getCause();
                if (throwable instanceof CommonException) {
                    throw (CommonException) throwable;
                } else if (throwable instanceof DuplicateKeyException) {
                    throw (DuplicateKeyException) throwable;
                } else {
                    throw new CommonException("error.executorError", e.getCause().getMessage());
                }
            } catch (InterruptedException e) {
                cancelTask(futures);
                Thread.currentThread().interrupt(); // 重置中断标识
                LOGGER.error("[>>Executor<<] Async task [{}] - [{}] were interrupted.", taskName, i);
                throw new CommonException(BaseConstants.ErrorCode.ERROR);
            }
        }
        LOGGER.info("[>>Executor<<] Finish async tasks , BatchTaskName: [{}], TaskSize: [{}]", taskName, size);
        return resultList;
    }

    /**
     * 根据一定周期输出线程池的状态
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     */
    public static void displayThreadPoolStatus(ThreadPoolExecutor threadPool, String threadPoolName) {
        displayThreadPoolStatus(threadPool, threadPoolName, RandomUtils.nextInt(60, 600), TimeUnit.SECONDS);
    }

    /**
     * 根据一定周期输出线程池的状态
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     * @param period         周期
     * @param unit           时间单位
     */
    public static void displayThreadPoolStatus(ThreadPoolExecutor threadPool, String threadPoolName, long period, TimeUnit unit) {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            String payload = "[>>ExecutorStatus<<] ThreadPool Name: [{}], Pool Status: [shutdown={}, Terminated={}], Pool Thread Size: {}, Largest Pool Size: {}, Active Thread Count: {}, Task Count: {}, Tasks Completed: {}, Tasks in Queue: {}";
            Object[] params = new Object[]{threadPoolName,
                    threadPool.isShutdown(), threadPool.isTerminated(), // 线程是否被终止
                    threadPool.getPoolSize(), // 线程池线程数量
                    threadPool.getLargestPoolSize(), // 线程最大达到的数量
                    threadPool.getActiveCount(), // 工作线程数
                    threadPool.getTaskCount(), // 总任务数
                    threadPool.getCompletedTaskCount(), // 已完成的任务数
                    threadPool.getQueue().size()};

            if (threadPool.getQueue().remainingCapacity() < 64) {
                LOGGER.warn(payload, params);
            } else {
                LOGGER.info(payload, params);
            }
        }, 0, period, unit);
    }

    /**
     * 添加Hook在Jvm关闭时优雅的关闭线程池
     *
     * @param threadPool     线程池
     * @param threadPoolName 线程池名称
     */
    public static void hookShutdownThreadPool(ExecutorService threadPool, String threadPoolName) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            LOGGER.info("[>>ExecutorShutdown<<] Start to shutdown the thead pool: [{}]", threadPoolName);
            // 使新任务无法提交
            threadPool.shutdown();
            try {
                // 等待未完成任务结束
                if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow(); // 取消当前执行的任务
                    LOGGER.warn("[>>ExecutorShutdown<<] Interrupt the worker, which may cause some task inconsistent. Please check the biz logs.");

                    // 等待任务取消的响应
                    if (!threadPool.awaitTermination(60, TimeUnit.SECONDS)) {
                        LOGGER.error("[>>ExecutorShutdown<<] Thread pool can't be shutdown even with interrupting worker threads, which may cause some task inconsistent. Please check the biz logs.");
                    }
                }
            } catch (InterruptedException ie) {
                // 重新取消当前线程进行中断
                threadPool.shutdownNow();
                LOGGER.error("[>>ExecutorShutdown<<] The current server thread is interrupted when it is trying to stop the worker threads. This may leave an inconsistent state. Please check the biz logs.");

                // 保留中断状态
                Thread.currentThread().interrupt();
            }

            LOGGER.info("[>>ExecutorShutdown<<] Finally shutdown the thead pool: [{}]", threadPoolName);
        }));
    }

    /**
     * 获取返回CPU核数
     *
     * @return 返回CPU核数，默认为8
     */
    public static int getCpuProcessors() {
        return Runtime.getRuntime() != null && Runtime.getRuntime().availableProcessors() > 0 ?
                Runtime.getRuntime().availableProcessors() : 8;
    }

    private static <T> void cancelTask(List<Future<T>> futures) {
        for (Future<T> future : futures) {
            if (!future.isDone()) {
                future.cancel(true);
            }
        }
    }

    /**
     * 线程池管理器
     */
    public static class ExecutorManager {

        private static final ConcurrentHashMap<String, ThreadPoolExecutor> EXECUTORS = new ConcurrentHashMap<>(8);

        /**
         * 向管理器注册线程池
         *
         * @param threadPoolName 线程池名称
         * @param executor       ThreadPoolExecutor
         */
        public static void registerThreadPoolExecutor(String threadPoolName, ThreadPoolExecutor executor) {
            EXECUTORS.put(threadPoolName, executor);
        }

        /**
         * 向管理器注册线程池，并监控线程池状态
         *
         * @param threadPoolName 线程池名称
         * @param executor       ThreadPoolExecutor
         */
        public static void registerAndMonitorThreadPoolExecutor(String threadPoolName, ThreadPoolExecutor executor) {
            EXECUTORS.put(threadPoolName, executor);
            CommonExecutor.displayThreadPoolStatus(executor, threadPoolName);
            CommonExecutor.hookShutdownThreadPool(executor, threadPoolName);
        }

        /**
         * 根据名称获取线程池
         *
         * @param threadPoolName 线程池名称
         */
        public static ThreadPoolExecutor getThreadPoolExecutor(String threadPoolName) {
            return EXECUTORS.get(threadPoolName);
        }

        /**
         * 获取所有已注册的线程池
         *
         * @return ThreadPoolExecutor
         */
        public static Map<String, ThreadPoolExecutor> getAllThreadPoolExecutor() {
            return ImmutableMap.copyOf(EXECUTORS);
        }

        /**
         * 根据名称移除已注册的线程池
         *
         * @param threadPoolName 线程池名称
         */
        public static void removeThreadPoolExecutor(String threadPoolName) {
            EXECUTORS.remove(threadPoolName);
        }
    }

}
