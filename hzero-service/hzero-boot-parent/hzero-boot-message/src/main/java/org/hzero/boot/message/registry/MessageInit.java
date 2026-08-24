package org.hzero.boot.message.registry;

import java.util.Map;

import org.hzero.boot.message.annotation.SocketHandler;
import org.hzero.boot.message.handler.ISocketHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.util.ProxyUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import io.choerodon.core.convertor.ApplicationContextHelper;

/**
 * 注解扫描
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/28 20:49
 */
@Component
public class MessageInit implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(MessageInit.class);

    @Override
    public void run(String... args) throws Exception {
        Map<String, Object> map = ApplicationContextHelper.getContext().getBeansWithAnnotation(SocketHandler.class);
        for (Object service : map.values()) {
            if (service instanceof ISocketHandler) {
                SocketHandler socketHandler = ProxyUtils.getUserClass(service).getAnnotation(SocketHandler.class);
                if (ObjectUtils.isEmpty(socketHandler)) {
                    logger.debug("could not get target bean , socketHandler : {}", service);
                } else {
                    SocketHandlerRegistry.addHandler(socketHandler.value(), service);
                }
            }
        }
    }
}
