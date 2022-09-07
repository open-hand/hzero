package org.hzero.autoconfigure.oauth;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.hzero.core.util.CommonExecutor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Oauth 自动化配置
 *
 * @author bojiangzhou 2018/10/25
 */
@ComponentScan(value = {
        "org.hzero.oauth.security",
        "org.hzero.oauth.api",
        "org.hzero.oauth.config",
        "org.hzero.oauth.domain",
        "org.hzero.oauth.infra",
})
@EnableFeignClients({"org.hzero.oauth"})
@EnableOAuth2Client
@EnableRedisHttpSession(maxInactiveIntervalInSeconds = OauthAutoConfiguration.MAX_INACTIVE_INTERVAL_IN_SECONDS)
@EnableConfigurationProperties
@Configuration
@EnableAsync
public class OauthAutoConfiguration {
    public static final int MAX_INACTIVE_INTERVAL_IN_SECONDS = 600;

    /**
     * 通用线程池
     */
    @Bean
    @Qualifier("commonAsyncTaskExecutor")
    public ThreadPoolExecutor commonAsyncTaskExecutor() {
        int size = CommonExecutor.getCpuProcessors() * 3;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(size, size, 30,
                TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(),
                new ThreadFactoryBuilder().setNameFormat("common-executor-%d").build());
        executor.allowCoreThreadTimeOut(true);
        CommonExecutor.displayThreadPoolStatus(executor, "Common-executor");
        CommonExecutor.hookShutdownThreadPool(executor, "Common-executor");

        return executor;
    }
}
