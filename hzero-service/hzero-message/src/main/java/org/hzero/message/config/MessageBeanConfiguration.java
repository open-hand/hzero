package org.hzero.message.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.hzero.message.app.service.RelSendMessageService;
import org.hzero.message.app.service.impl.RelSendMessageServiceImpl;

/**
 * 自动化配置类
 *
 * @author bojiangzhou 2020/05/09
 */
@Configuration
public class MessageBeanConfiguration {

    @Bean
    @ConditionalOnMissingBean(RelSendMessageService.class)
    public RelSendMessageService relSendMessageService() {
        return new RelSendMessageServiceImpl();
    }

}
