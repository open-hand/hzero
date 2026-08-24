package org.hzero.boot.autoconfigure.imported;

import org.hzero.boot.imported.config.ImportConfig;
import org.hzero.boot.imported.infra.feign.TemplateRemoteService;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 自动装配
 *
 * @author shuangfei.zhu@hand-china.com 2018/11/27 16:16
 */
@Configuration
@ComponentScan(basePackages = "org.hzero.boot.imported")
@EnableFeignClients(basePackageClasses = TemplateRemoteService.class)
public class ImportAutoConfiguration {


    @Bean("import-executor")
    public AsyncTaskExecutor asyncTaskExecutor(ImportConfig importConfig) {
        ImportConfig.ThreadPoolProperties threadPoolProperties = importConfig.getThreadPoolProperties();
        if (threadPoolProperties == null) {
            threadPoolProperties = new ImportConfig.ThreadPoolProperties();
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
