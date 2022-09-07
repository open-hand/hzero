package org.hzero.autoconfigure.swagger;

import org.hzero.swagger.config.SwaggerProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * IAM 自动化配置
 *
 * @author bojiangzhou 2018/10/25
 */
@ComponentScan(value = {
    "org.hzero.swagger.api",
    "org.hzero.swagger.app",
    "org.hzero.swagger.config",
    "org.hzero.swagger.domain",
    "org.hzero.swagger.infra",
})
@EnableFeignClients({"org.hzero.swagger"})
@EnableScheduling
@EnableAsync
@Configuration
@EnableConfigurationProperties(SwaggerProperties.class)
public class SwaggerAutoConfiguration {

    /**
     * 用于权限刷新的异步线程池
     * @return
     */
    @Bean
    @Qualifier("swaggerRefreshAsyncExecutor")
    public ThreadPoolExecutor swaggerRefreshAsyncExecutor() {
        return new ThreadPoolExecutor(4, 8, 0, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(), runnable -> new Thread(runnable, "Swagger-Refresh-Executor"));
    }

}
