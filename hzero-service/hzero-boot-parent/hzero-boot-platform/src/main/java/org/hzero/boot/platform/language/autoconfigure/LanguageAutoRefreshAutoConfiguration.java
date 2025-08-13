package org.hzero.boot.platform.language.autoconfigure;

import org.hzero.boot.platform.language.LanguageAutoRefreshTask;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 数据屏蔽自动化配置
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 14:07
 */
@Configuration
@EnableConfigurationProperties(LanguageAutoRefreshProperties.class)
@ConditionalOnProperty(
        prefix = LanguageAutoRefreshProperties.LANGUAGE_AUTO_REFRESH_PROPERTIES_PREFIX,
        name = "enable",
        havingValue = "true")
@ConditionalOnClass(RedisHelper.class)
public class LanguageAutoRefreshAutoConfiguration {

    @Bean
    public LanguageAutoRefreshTask languageAutoRefreshTask(RedisHelper redisHelper,
                                                           LanguageAutoRefreshProperties languageAutoRefreshProperties,
                                                           HZeroService.Platform platform) {
        return new LanguageAutoRefreshTask(redisHelper, languageAutoRefreshProperties);
    }

    @Bean
    public ScheduledExecutorService languageAutoRefreshExecutor(LanguageAutoRefreshTask task) {
        ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        Duration interval = task.getProperties().getInterval();
        scheduledExecutorService.scheduleAtFixedRate(task, 0L, interval.getSeconds(), TimeUnit.SECONDS);
        return scheduledExecutorService;
    }
}
