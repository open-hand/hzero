package org.hzero.boot.scheduler.configure;

import java.util.concurrent.*;

import org.hzero.boot.scheduler.infra.feign.SchedulerFeignClient;
import org.hzero.boot.scheduler.infra.handler.ExecutionHandler;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * 自动装配
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/27 16:16
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.boot.scheduler")
@EnableFeignClients(basePackageClasses = SchedulerFeignClient.class)
public class ExecutorAutoConfigure {

    /**
     * 调度任务执行线程池
     */
    @Bean("jobAsyncExecutor")
    public ExecutorService commonAsyncTaskExecutor(SchedulerConfig config) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("scheduler-job-run-%d").build();
        ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getCorePoolSize(), config.getMaxPoolSize(), 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(1), threadFactory);
        executor.setRejectedExecutionHandler(new ExecutionHandler());
        return executor;
    }
}
