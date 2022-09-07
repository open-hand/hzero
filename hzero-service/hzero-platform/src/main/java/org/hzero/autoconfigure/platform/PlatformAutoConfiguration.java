package org.hzero.autoconfigure.platform;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.choerodon.resource.annoation.EnableChoerodonResourceServer;
import org.hzero.boot.platform.language.autoconfigure.LanguageAutoRefreshProperties;
import org.hzero.common.HZeroService;
import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.hzero.core.redis.RedisHelper;
import org.hzero.core.util.CommonExecutor;
import org.hzero.platform.infra.mapper.HpfmLanguageMapper;
import org.hzero.platform.infra.properties.DataHierarchyProperties;
import org.hzero.platform.infra.properties.PlatformProperties;
import org.hzero.platform.infra.task.LanguageAutoUpdateTask;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import java.time.Duration;
import java.util.concurrent.*;

/**
 * @author bojiangzhou 2018/10/25
 */
@ComponentScan(value = {"org.hippius.wd", "org.hzero.platform.api", "org.hzero.platform.app",
        "org.hzero.platform.config", "org.hzero.platform.domain", "org.hzero.platform.infra"})
@EnableFeignClients({"org.hzero.platform", "io.choerodon", "org.hzero.plugin"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableChoerodonResourceServer
@EnableObjectMapper
@EnableAsync
@EnableConfigurationProperties({PlatformProperties.class, DataHierarchyProperties.class})
public class PlatformAutoConfiguration {

    /**
     * 通用线程池
     */
    @Bean
    @Qualifier("commonAsyncTaskExecutor")
    public ThreadPoolExecutor commonAsyncTaskExecutor() {
        int coreSize = CommonExecutor.getCpuProcessors();
        int maxSize = coreSize * 8;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize, maxSize,
                30, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(16),
                new ThreadFactoryBuilder().setNameFormat("CommonExecutor-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        CommonExecutor.displayThreadPoolStatus(executor, "HpfmCommonExecutor");
        CommonExecutor.hookShutdownThreadPool(executor, "HpfmCommonExecutor");

        return executor;
    }

    @Bean
    @ConditionalOnProperty(
            prefix = LanguageAutoRefreshProperties.LANGUAGE_AUTO_REFRESH_PROPERTIES_PREFIX,
            name = "enable",
            havingValue = "true")
    public LanguageAutoUpdateTask languageAutoUpdateTask(RedisHelper redisHelper,
                                                         LanguageAutoRefreshProperties languageAutoRefreshProperties,
                                                         HpfmLanguageMapper languageMapper,
                                                         HZeroService.Platform platform) {
        return new LanguageAutoUpdateTask(redisHelper, languageAutoRefreshProperties, languageMapper);
    }

    @Bean
    @ConditionalOnBean(LanguageAutoUpdateTask.class)
    public ScheduledExecutorService languageAutoUpdateExecutor(LanguageAutoUpdateTask task) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Duration interval = task.getProperties().getInterval();
        scheduledExecutorService.scheduleAtFixedRate(task, 0L, interval.getSeconds(), TimeUnit.SECONDS);
        return scheduledExecutorService;
    }

}
