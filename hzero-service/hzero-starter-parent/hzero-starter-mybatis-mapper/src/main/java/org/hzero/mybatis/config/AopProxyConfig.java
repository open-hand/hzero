package org.hzero.mybatis.config;

import org.hzero.mybatis.config.aspect.TenantLimitedAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * 配置AOP<p></p>
 * <ul>
 * <li>关闭GCLIB</li>
 * <li>开启ExposeProxy</li>
 * </ul>
 *
 * @author gaokuo.dai@hand-china.com 2018年8月12日下午12:44:09
 */
@Configuration
@EnableAspectJAutoProxy(exposeProxy = true)
public class AopProxyConfig {

    @Bean
    public TenantLimitedAspect tenantLimitedAspect() {
        return new TenantLimitedAspect();
    }
}
