package org.hzero.export.config;

import org.springframework.beans.factory.SmartInitializingSingleton;

import org.hzero.core.message.MessageAccessor;

/**
 * 初始化配置
 *
 * @author bojiangzhou 2019/07/23
 */
public class ExportInitializeConfig implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_export");
    }
}
