package org.hzero.lock.factory;

import java.util.HashMap;
import java.util.Map;

import org.hzero.lock.enums.ServerPattern;

import io.choerodon.core.exception.CommonException;

/**
 * 
 * Redis服务模式工厂类
 * 
 * @author xianzhi.chen@hand-china.com 2019年4月4日下午2:48:10
 */
public class ServerPatternFactory {
    
    private static Map<String, ServerPattern> serverPatternMap = new HashMap<>();

    static {
        serverPatternMap.put(ServerPattern.SINGLE.getPattern(), ServerPattern.SINGLE);
        serverPatternMap.put(ServerPattern.CLUSTER.getPattern(), ServerPattern.CLUSTER);
        serverPatternMap.put(ServerPattern.MASTER_SLAVE.getPattern(), ServerPattern.MASTER_SLAVE);
        serverPatternMap.put(ServerPattern.REPLICATED.getPattern(), ServerPattern.REPLICATED);
        serverPatternMap.put(ServerPattern.SENTINEL.getPattern(), ServerPattern.SENTINEL);
    }

    /**
     * 根据字符串模式标识返回服务器模式枚举类
     * 
     * @param pattern
     * @return
     */
    public static ServerPattern getServerPattern(String pattern) throws CommonException {
        ServerPattern serverPattern = serverPatternMap.get(pattern);
        if (serverPattern == null) {
            throw new CommonException("没有找到相应的服务器模式,请检测参数是否正常,pattern的值为:" + pattern);
        }
        return serverPattern;
    }
}
