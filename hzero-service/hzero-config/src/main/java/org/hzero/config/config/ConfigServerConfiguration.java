package org.hzero.config.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.config.server.config.ConfigServerEncryptionConfiguration;
import org.springframework.cloud.config.server.config.ConfigServerMvcConfiguration;
import org.springframework.cloud.config.server.config.ConfigServerProperties;
import org.springframework.cloud.config.server.config.ResourceRepositoryConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Config 配置
 *
 * @author bojiangzhou 2018/12/18
 */
@Configuration
@EnableConfigurationProperties({
    ConfigServerProperties.class,
})
@Import({
    CustomEnvironmentRepositoryConfig.class,
    ResourceRepositoryConfiguration.class,
    ConfigServerEncryptionConfiguration.class,
    ConfigServerMvcConfiguration.class
})
public class ConfigServerConfiguration {

}
