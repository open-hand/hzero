package org.hzero.boot.report.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 自动装配
 *
 * @author shuangfei.zhu@hand-china.com 2020/06/23 16:16
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.boot.report")
public class ExecutorAutoConfigure {

    @Bean("report-client-executor")
    public AsyncTaskExecutor asyncTaskExecutor(ReportClientConfig config) {
        ReportClientConfig.ThreadPoolProperties threadPoolProperties = config.getThreadPoolProperties();
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
