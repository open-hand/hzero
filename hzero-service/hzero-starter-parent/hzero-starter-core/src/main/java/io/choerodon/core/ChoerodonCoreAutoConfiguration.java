package io.choerodon.core;

import io.choerodon.core.config.RefreshConfigEndpoint;
import io.choerodon.core.convertor.ApplicationContextHelper;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * core包自动配置bean
 * @author flyleft
 */
@Configuration
public class ChoerodonCoreAutoConfiguration {

    @Bean
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }

    @Bean
    public RefreshConfigEndpoint endpoint(ContextRefresher contextRefresher) {
        return new RefreshConfigEndpoint(contextRefresher);
    }

}
