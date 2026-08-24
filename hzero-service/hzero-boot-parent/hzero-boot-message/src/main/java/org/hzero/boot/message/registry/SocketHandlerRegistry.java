package org.hzero.boot.message.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description
 *
 * @author shuangfei.zhu@hand-china.com 2019/08/28 20:46
 */
public class SocketHandlerRegistry {

    private SocketHandlerRegistry() {
    }

    private static final Map<String, Object> HANDLER_MAP = new ConcurrentHashMap<>();

    public static void addHandler(String key, Object handler) {
        HANDLER_MAP.put(key, handler);
    }

    public static Object getHandler(String key) {
        return HANDLER_MAP.get(key);
    }
}
