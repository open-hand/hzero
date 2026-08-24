package org.hzero.scheduler.config;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import org.hzero.core.message.MessageAccessor;

/**
 * 初始化配置
 */
@Component
public class SchedulerInitializeConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_hsdr");
    }
}
