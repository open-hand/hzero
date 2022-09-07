package org.hzero.platform.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.core.message.MessageAccessor;

/**
 *
 * @author bojiangzhou
 */
@Configuration
public class PlatformConfiguration {

    @Bean
    public SmartInitializingSingleton hpfmSmartInitializingSingleton() {
        return () -> {
            // 加入消息文件
            MessageAccessor.addBasenames("classpath:messages/messages_hpfm");
        };
    }
}
