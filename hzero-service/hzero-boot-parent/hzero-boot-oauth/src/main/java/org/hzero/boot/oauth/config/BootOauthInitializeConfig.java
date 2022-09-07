package org.hzero.boot.oauth.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import org.hzero.core.message.MessageAccessor;

@Component
public class BootOauthInitializeConfig implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_pwd");
    }
}
