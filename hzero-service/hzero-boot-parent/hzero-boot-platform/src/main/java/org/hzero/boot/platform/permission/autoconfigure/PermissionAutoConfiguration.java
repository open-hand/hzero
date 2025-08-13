package org.hzero.boot.platform.permission.autoconfigure;

import org.hzero.boot.platform.permission.tenant.TemporaryTenantHelper;
import org.hzero.boot.platform.permission.tenant.TemporaryTenantProperties;
import org.hzero.common.HZeroService;
import org.hzero.core.redis.RedisHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * 数据屏蔽自动化配置
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 14:07
 */
@Configuration
@EnableConfigurationProperties(TemporaryTenantProperties.class)
@ConditionalOnProperty(prefix = "hzero.permission", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PermissionAutoConfiguration {

    @Value(HZeroService.Platform.NAME)
    private String serviceName;

    @Bean
    @ConditionalOnMissingBean
    public TemporaryTenantHelper temporaryTenantHelper(RedisHelper redisHelper, TemporaryTenantProperties temporaryTenantProperties) {
        return new TemporaryTenantHelper(serviceName, redisHelper, temporaryTenantProperties);
    }
}
