package org.hzero.feign.aspect;

import com.netflix.hystrix.strategy.concurrency.HystrixRequestVariableDefault;

/**
 * 存储 Feign 调用的服务名
 */
public class FeignVariableHolder {

    private FeignVariableHolder(){}

    public static final HystrixRequestVariableDefault<String> FEIGN_SERVICE_NAME = new HystrixRequestVariableDefault<>();

}
