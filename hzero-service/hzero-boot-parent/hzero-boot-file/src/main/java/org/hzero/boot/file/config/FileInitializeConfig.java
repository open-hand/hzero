package org.hzero.boot.file.config;

import org.springframework.beans.factory.InitializingBean;

import org.hzero.core.message.MessageAccessor;

/**
 * 初始化配置
 */
public class FileInitializeConfig implements InitializingBean {

    @Override
    public void afterPropertiesSet() {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_boot_file");
    }
}
