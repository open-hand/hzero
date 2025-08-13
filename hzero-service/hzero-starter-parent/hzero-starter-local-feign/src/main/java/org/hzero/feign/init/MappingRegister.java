package org.hzero.feign.init;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.hzero.core.util.Pair;
import org.springframework.web.method.HandlerMethod;

/**
 * mapping存储
 *
 * @author shuangfei.zhu@hand-china.com 2019/05/20 17:44
 */
public class MappingRegister {

    private MappingRegister() {
    }

    /**
     * requestType, url   HandlerMethod
     */
    private static Map<Pair, HandlerMethod> methods = new ConcurrentHashMap<>(16);

    public static HandlerMethod getMethod(String requestType, String url) {
        Pair pair = Pair.of(requestType, url);
        return methods.get(pair);
    }

    static void addMethods(String requestType, String url, HandlerMethod handlerMethod) {
        methods.put(Pair.of(requestType, url), handlerMethod);
    }
}
