package org.hzero.boot.iam.field.config;

import org.hzero.boot.iam.field.FieldPermissionAspect;
import org.hzero.boot.iam.field.handler.DesensitizationFieldHandler;
import org.hzero.boot.iam.field.handler.HiddenFieldHandler;
import org.hzero.boot.iam.field.handler.ResetFieldHandler;
import org.hzero.core.redis.RedisHelper;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

/**
 * 字段权限配置类
 *
 * @author qingsheng.chen@hand-china.com
 */
@ConditionalOnProperty(value = "hzero.field-permission.enable", havingValue = "true")
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableConfigurationProperties(FieldPermissionProperty.class)
public class FieldPermissionConfiguration {

    @Bean
    public FieldPermissionAspect fieldPermissionAspect(RequestMappingHandlerMapping requestMappingHandlerMapping,
                                                       RedisHelper redisHelper,
                                                       Environment environment) {
        return new FieldPermissionAspect(requestMappingHandlerMapping, redisHelper, environment.getProperty("spring.application.name"));
    }

    @Bean
    public ResetFieldHandler resetFieldHandler(FieldPermissionAspect fieldPermissionAspect, RedisHelper redisHelper, FieldPermissionProperty fieldPermissionProperty) {
        ResetFieldHandler resetFieldHandler = new ResetFieldHandler(redisHelper, fieldPermissionProperty);
        fieldPermissionAspect.addHandler(resetFieldHandler);
        return resetFieldHandler;
    }

    @Bean
    public HiddenFieldHandler hiddenFieldHandler(FieldPermissionAspect fieldPermissionAspect) {
        HiddenFieldHandler hiddenFieldHandler = new HiddenFieldHandler();
        fieldPermissionAspect.addHandler(hiddenFieldHandler);
        return hiddenFieldHandler;
    }

    @Bean
    public DesensitizationFieldHandler desensitizationFieldHandler(FieldPermissionAspect fieldPermissionAspect) {
        DesensitizationFieldHandler desensitizationFieldHandler = new DesensitizationFieldHandler();
        fieldPermissionAspect.addHandler(desensitizationFieldHandler);
        return desensitizationFieldHandler;
    }
}
