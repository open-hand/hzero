package org.hzero.imported.infra.init;

import org.hzero.core.message.MessageAccessor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

/**
 * 初始化缓存
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/24 16:07
 */
@Component
public class ImportRedisInit implements InitializingBean {

    @Override
    public void afterPropertiesSet() throws Exception {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_himp");
    }
}
