package io.choerodon.mybatis;

import org.hzero.core.message.MessageAccessor;
import org.springframework.beans.factory.SmartInitializingSingleton;

/**
 * 加载本地化资源
 *
 * @author qingsheng.chen@hand-china.com
 */
public class MybatisLocaleAutoConfiguration implements SmartInitializingSingleton {

    @Override
    public void afterSingletonsInstantiated() {
        // 加入消息文件
        MessageAccessor.addBasenames("classpath:messages/messages_mybatis");
    }
}
