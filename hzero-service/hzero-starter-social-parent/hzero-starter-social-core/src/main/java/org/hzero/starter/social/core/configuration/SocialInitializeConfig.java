package org.hzero.starter.social.core.configuration;

import org.springframework.beans.factory.InitializingBean;

import org.hzero.core.message.MessageAccessor;

/**
 * 初始化配置
 *
 * @author bojiangzhou 2019/07/23
 */
public class SocialInitializeConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/message_social");
    }
}
