package org.hzero.core.endpoint;

import org.hzero.core.HZeroAutoConfiguration;
import org.hzero.core.endpoint.client.StringHttpTransporter;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 默认{@link HttpTransporter}自动配置类
 * @author XCXCXCXCX
 * @date 2020/4/22 12:47 下午
 */
@Configuration
@AutoConfigureAfter(HZeroAutoConfiguration.class)
public class DefaultHttpTransporterAutoConfiguration {
    @Bean
    public StringHttpTransporter stringHttpTransporter(){
        return new StringHttpTransporter();
    }
}
