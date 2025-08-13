package org.hzero.core.modular;

import io.choerodon.swagger.swagger.extra.ExtraDataInitialization;
import org.hzero.core.modular.initialize.MergeExtraDataInitialization;
import org.hzero.core.modular.initialize.ModularExtraDataInitialization;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 服务合并自动配置类
 * @author XCXCXCXCX
 * @date 2019/8/13
 */
@Configuration
@EnableConfigurationProperties(ModularProperties.class)
public class ModularAutoConfiguration {

    @Bean
    public ExtraDataInitialization mergeExtraDataInitialization(ModularProperties properties){
        return new MergeExtraDataInitialization(properties);
    }

    @Bean
    public ExtraDataInitialization modularExtraDataInitialization(ModularProperties properties){
        return new ModularExtraDataInitialization(properties);
    }

}
