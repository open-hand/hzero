package org.hzero.boot.platform.data.autoconfigure;

import io.choerodon.mybatis.MybatisMapperAutoConfiguration;
import org.hzero.boot.platform.data.permission.builder.PermissionSqlBuilder;
import org.hzero.boot.platform.data.permission.interceptor.FilterSqlInterceptor;
import org.hzero.boot.platform.data.permission.repository.DefaultPermissionSqlRepository;
import org.hzero.boot.platform.data.permission.repository.PermissionSqlRepository;
import org.hzero.core.redis.RedisHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.HashSet;

/**
 * <p>
 * 数据屏蔽自动化配置
 * </p>
 *
 * @author yunxiang.zhou01@hand-china.com 2018/07/31 14:07
 */
@Configuration
@EnableConfigurationProperties(PermissionDataProperties.class)
@ConditionalOnClass(MybatisMapperAutoConfiguration.class)
@ConditionalOnProperty(prefix = "hzero.data.permission", name = "enabled", havingValue = "true", matchIfMissing = true)
public class PermissionDataAutoConfiguration {


    @Bean
    @ConditionalOnMissingBean
    public PermissionSqlRepository permissionSqlRepository(RedisHelper redisHelper, PermissionDataProperties permissionDataProperties) {
        return new DefaultPermissionSqlRepository(redisHelper, permissionDataProperties.getLocalCacheExpirationTime());
    }

    @Bean
    @ConditionalOnMissingBean
    public PermissionSqlBuilder permissionDataInterceptor(ApplicationContext applicationContext,
                                                          PermissionSqlRepository permissionSqlRepository,
                                                          PermissionDataProperties permissionDataProperties) {
        // 检测上下文中的拦截器
        String[] beanNames = applicationContext.getBeanNamesForType(FilterSqlInterceptor.class);
        return new PermissionSqlBuilder(permissionSqlRepository, permissionDataProperties, new HashSet<>(Arrays.asList(beanNames)));
    }
}
