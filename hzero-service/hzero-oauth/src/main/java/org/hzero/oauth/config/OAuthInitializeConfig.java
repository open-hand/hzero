package org.hzero.oauth.config;

import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.stereotype.Component;

import org.hzero.core.message.MessageAccessor;

/**
 * 初始化配置
 *
 * @author bojiangzhou 2019/07/23
 */
@Component
public class OAuthInitializeConfig implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_hoth");
    }
}
