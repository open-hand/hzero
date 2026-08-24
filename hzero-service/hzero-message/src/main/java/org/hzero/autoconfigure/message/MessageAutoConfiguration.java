package org.hzero.autoconfigure.message;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.*;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.hzero.message.config.MessageBeanConfiguration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;


@ComponentScan(value = {
    "org.hzero.message.api",
    "org.hzero.message.app",
    "org.hzero.message.config",
    "org.hzero.message.domain",
    "org.hzero.message.infra",
})
@EnableAsync
@Configuration
@EnableObjectMapper
@EnableChoerodonResourceServer
@EnableFeignClients({"org.hzero.message"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@Import(MessageBeanConfiguration.class)
public class MessageAutoConfiguration {

    @Bean("commonAsyncTaskExecutor")
    public AsyncTaskExecutor commonAsyncTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("common-executor");
        executor.setCorePoolSize(20);
        executor.setMaxPoolSize(200);
        executor.setQueueCapacity(5000);
        return executor;
    }
}
