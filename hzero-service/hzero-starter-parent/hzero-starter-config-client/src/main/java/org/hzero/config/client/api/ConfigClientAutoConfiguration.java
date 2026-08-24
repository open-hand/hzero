package org.hzero.config.client.api;

import org.hzero.config.client.api.impl.CompositeConfigClient;
import org.hzero.config.client.api.impl.DefaultConfigClient;
import org.hzero.config.client.api.impl.NacosConfigClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 配置中心客户端自动配置类
 * @author XCXCXCXCX
 * @date 2019/9/23
 */
@Configuration
public class ConfigClientAutoConfiguration {

    @Primary
    @Bean
    public ConfigClient compositeConfigClient(Optional<List<ConfigClient>> optional){
        return new CompositeConfigClient(optional.orElse(new ArrayList<>()));
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.config.client.ConfigServicePropertySourceLocator")
    public ConfigClient defaultConfigClient(){
        return new DefaultConfigClient();
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.cloud.alibaba.nacos.client.NacosPropertySourceLocator")
    public ConfigClient nacosConfigClient(){
        return new NacosConfigClient();
    }

}
