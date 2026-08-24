package io.choerodon.resource;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.choerodon.resource.permission.PublicPermissionOperationPlugin;
import io.choerodon.swagger.SwaggerConfig;

/**
 * 配置jwtToken的验证规则
 *
 * @author wuguokai
 */
@Configuration
@AutoConfigureBefore(JacksonAutoConfiguration.class)
@AutoConfigureAfter(SwaggerConfig.class)
public class ResourceServerConfig {

    /**
     * 扫描public接口
     *
     * @return Bean
     */
    @Bean
    public PublicPermissionOperationPlugin permissionSwaggerOperationPlugin() {
        return new PublicPermissionOperationPlugin();
    }
}